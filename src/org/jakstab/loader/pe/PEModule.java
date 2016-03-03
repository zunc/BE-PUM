/*
 * PEModule.java - This file is part of the Jakstab project.
 * 
 * Copyright 2007-2012 Johannes Kinder <jk@jakstab.org>
 * Copyright (C) 2003 The University of Arizona
 *
 * The original code for this class was taken from "MBEL: The Microsoft 
 * Bytecode Engineering Library" and modified for use with Jakstab.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, see <http://www.gnu.org/licenses/>.
 */

package org.jakstab.loader.pe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jakstab.Options;
import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.SymbolFinder;
import org.jakstab.loader.BinaryParseException;
import org.jakstab.loader.ExportedSymbol;
import org.jakstab.loader.UnresolvedSymbol;
import org.jakstab.loader.UnresolvedSymbol.AddressingType;
import org.jakstab.ssl.Architecture;
import org.jakstab.util.BinaryFileInputBuffer;
import org.jakstab.util.FastSet;
import org.jakstab.util.Logger;
import org.jakstab.util.Pair;

/**
 * This class is used to parse and hold a PE/COFF file.
 * 
 * @author Michael Stepp, Johannes Kinder
 */
public class PEModule extends AbstractCOFFModule {

	private final static Logger logger = Logger.getLogger(PEModule.class);

	private MSDOS_Stub msdos_stub;
	private PE_Header pe_header;
	private ExportEntry[] exportEntries;
	private Map<AbsoluteAddress, Pair<String, String>> importTable;
	private String fileName;
	private final PESymbolFinder symbolFinder;
	
	/**
	 * Parses a PEModule from a specified file
	 */
	public PEModule(File peFile, Architecture arch) throws IOException, BinaryParseException {

		InputStream inStream = new FileInputStream(peFile);
		fileName = peFile.getName();

		inBuf = new BinaryFileInputBuffer(inStream);
		try {
			msdos_stub = new MSDOS_Stub(inBuf);
			inBuf.seek(msdos_stub.getHeaderAddress());
		} catch (Exception e) {
			System.out.println("MS-DOS executables are not supported.");
			inBuf.seek(getPEHeaderAddress(inBuf));
		}

		// Verify PE signature ///////////

		if (!inBuf.match(PE_Header.PE_TAG)) {
			throw new BinaryParseException("PEModule: Missing PE signature");
		}
		// ////////////////////////////////
		coff_header = new COFF_Header(inBuf);
		// long optionalHeaderPos = inBuf.getCurrent();
		// long sectionPos = optionalHeaderPos +
		// coff_header.getSizeOfOptionalHeader();
		long posOptionalHeader = inBuf.getCurrent();
		pe_header = new PE_Header(inBuf);
		long alignment = pe_header.getFileAlignment();
		long posSectionHeader = posOptionalHeader + coff_header.getSizeOfOptionalHeader();
		// /// Parse Section Headers and sections /////////////////////////
		// if (sectionPos != tempSectionPos)
		inBuf.seek(posSectionHeader);
		section_headers = new SectionHeader[coff_header.getNumberOfSections()];
		for (int i = 0; i < coff_header.getNumberOfSections(); i++) {
			section_headers[i] = new SectionHeader(inBuf, alignment);
		}
		// if (sectionPos != tempSectionPos)
		// inBuf.seek(tempSectionPos);
		// ///////////////////////////////////////////////////////////////

		// ///////////////////////////////////////////////////////////////
		// Parse exports and build export table
		long expTableRVA = pe_header.getDataDirectory()[ImageDataDirectory.EXPORT_TABLE_INDEX].VirtualAddress;
		if (expTableRVA > 0) { // We have an export table
			logger.debug("-- Reading export table...");
			inBuf.seek(getFilePointerFromRVA(expTableRVA));
			ImageExportDirectory imageExportDirectory = new ImageExportDirectory(inBuf);

			inBuf.seek(getFilePointerFromRVA(imageExportDirectory.AddressOfFunctions));
			// Parse EAT
			ExportEntry[] tmpEntries = new ExportEntry[(int) imageExportDirectory.NumberOfFunctions];
			int eatEntries = 0;
			for (int i = 0; i < tmpEntries.length; i++) {
				long rva = inBuf.readDWORD();
				if (rva > 0) {
					tmpEntries[i] = new ExportEntry((int) (i + imageExportDirectory.Base), new AbsoluteAddress(rva
							+ getBaseAddress()));
					eatEntries++;
				}
			}

			long namePtr = getFilePointerFromRVA(imageExportDirectory.AddressOfNames);
			long ordPtr = getFilePointerFromRVA(imageExportDirectory.AddressOfNameOrdinals);
			for (int i = 0; i < imageExportDirectory.NumberOfNames; i++) {
				// read next ENT entry
				inBuf.seek(namePtr);
				long rva = inBuf.readDWORD();
				namePtr = inBuf.getCurrent();
				// read export name
				inBuf.seek(getFilePointerFromRVA(rva));
				String expName = inBuf.readASCII();
				// read next EOT entry
				inBuf.seek(ordPtr);
				int ord = inBuf.readWORD();
				ordPtr = inBuf.getCurrent();
				tmpEntries[ord].setName(expName);
			}
			exportEntries = new ExportEntry[eatEntries];
			int j = 0;
			for (int i = 0; i < tmpEntries.length; i++) {
				if (tmpEntries[i] != null) {
					exportEntries[j++] = tmpEntries[i];
				}
			}
			logger.debug("-- Got " + exportEntries.length + " exported symbols.");
		} else {
			logger.debug("-- File contains no exports");
		}

		// ///////////////////////////////////////////////////////////////
		// Parse imports and build import table
		importTable = new HashMap<AbsoluteAddress, Pair<String, String>>();
		long impTableRVA = pe_header.getDataDirectory()[ImageDataDirectory.IMPORT_TABLE_INDEX].VirtualAddress;
		if (impTableRVA > 0) { // We have an import table
			logger.debug("-- Reading image import descriptors...");
			System.out.println("-- Reading image import descriptors....");
			long r = getFilePointerFromRVA(impTableRVA);

			if (r < 0) {
				logger.debug("-- Got " + importTable.size() + " imported function symbols.");
				symbolFinder = new PESymbolFinder(this);
				return;
			}
			inBuf.seek(r);
			List<ImageImportDescriptor> imageImportDescriptors = new LinkedList<ImageImportDescriptor>();
			try {
				while (true) {
					ImageImportDescriptor cur = new ImageImportDescriptor(inBuf);
					if (cur.isZero()) {
						break;
					}
					imageImportDescriptors.add(cur);
				}
			} catch (IOException e) {
				logger.fatal("IOException while parsing ImageImportDescriptor !", e);
				// e.printStackTrace();
				// return;
			}
			try {
				for (ImageImportDescriptor descriptor : imageImportDescriptors) {
					long t = getFilePointerFromRVA(descriptor.Name);
					if (t < 0) {
						inBuf.seek(descriptor.Name);
					} else {
						inBuf.seek(t);
					}
					String libraryFileName = inBuf.readASCII();
					logger.debug("-- Parsing imports from " + libraryFileName + "...");
					// Normalize filenames to lower case
					libraryFileName = libraryFileName.toLowerCase();

					// Check if the library is bound.
					boolean bound = descriptor.TimeDateStamp != 0 || descriptor.OriginalFirstThunk != 0;

					/* Read Import Address Table or Import Name Table */
					long iatFilePtr;
					// import names will be associated to IAT addresses in any
					// case
					// AbsoluteAddress iatAddress = (new RVAPointer(this,
					// descriptor.FirstThunk)).getVAPointer();
					AbsoluteAddress iatAddress = new AbsoluteAddress(descriptor.FirstThunk + getBaseAddress());
					if (bound) {
						iatFilePtr = getFilePointerFromRVA(descriptor.OriginalFirstThunk);
						if (iatFilePtr < 0) {
							long temp = descriptor.OriginalFirstThunk & 0xFFFF;
							iatFilePtr = getFilePointerFromRVA(temp);
							iatAddress = new AbsoluteAddress(temp + getBaseAddress());
						}
					} else {
						iatFilePtr = getFilePointerFromRVA(descriptor.FirstThunk);
						if (iatFilePtr < 0) {
							long temp = descriptor.FirstThunk & 0xFFFF;
							iatFilePtr = getFilePointerFromRVA(temp);
							iatAddress = new AbsoluteAddress(temp + getBaseAddress());
						}
					}

					while (true) {
						inBuf.seek(iatFilePtr);
						long thunk = inBuf.readDWORD();
						iatFilePtr = inBuf.getCurrent(); // Save buffer position
						if (thunk == 0) {
							break;
						}
						if ((thunk & 0x80000000) != 0) {
							/*
							 * Thunk contains ordinal value in low 31 bits. (for
							 * 64 bit files this would be the lower 63 bits.
							 */				
							
							int ord = (int) (thunk & 0x7FFFFFFF);												
							String ordName = extractNameFromOrdinalNumber(libraryFileName, ord);
							if (ordName == null) {
								ordName = "ord(" + ord + ")";
							}
							importTable.put(iatAddress, Pair.create(libraryFileName, ordName));
						} else {
							/*
							 * Thunk contains an RVA of either a
							 * IMAGE_IMPORT_BY_NAME structure [word (ord hint) ,
							 * string (function name)] or to a forwarder string.
							 * Forwarding not supported at the moment!
							 */

							long rva = getFilePointerFromRVA(thunk);
							if (rva < 0) {
								// throw new
								// BinaryParseException("RVA in thunk points outside of image!");
								inBuf.seek(thunk + 2);
								String funcName = inBuf.readASCII();
								importTable.put(iatAddress, Pair.create(libraryFileName, funcName));
							} else {
								// Just skip the ord hint (WORD), we don't need it.
								inBuf.seek(rva + 2);
								String funcName = inBuf.readASCII();
								importTable.put(iatAddress, Pair.create(libraryFileName, funcName));
							}
						}
						// Advance IAT entry by one DWORD
						iatAddress = new AbsoluteAddress(iatAddress.getValue() + 4);
					}
				}
			} catch (Exception e) {
				logger.fatal("IOException while parsing Import Table !", e);
			}
		}

		// TODO: Parse delayload imports

		logger.debug("-- Got " + importTable.size() + " imported function symbols.");
		// System.out.println("-- Got " + importTable.size()
		// + " imported function symbols.");

		symbolFinder = new PESymbolFinder(this);
	}

	private String extractNameFromOrdinalNumber(String libraryFileName, int ord) {
		// TODO Auto-generated method stub
		File f = new File(Program.pathLibrary);
		final String t = libraryFileName.toLowerCase();
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
		    @Override
			public boolean accept(File dir, String name) {
		        return name.toLowerCase().equals(t);
		    }
		});
		
		if (matchingFiles.length > 0) {
			return extractOrdinalName(matchingFiles[0], ord);
		}
		
		return null;
	}

	private String extractOrdinalName(File file, int ordinal){
		// TODO Auto-generated method stub
		if (file != null) {
			try {
				InputStream inStream = new FileInputStream(file);				
				BinaryFileInputBuffer buf = new BinaryFileInputBuffer(inStream);
				try {
					MSDOS_Stub msdos = new MSDOS_Stub(buf);
					buf.seek(msdos.getHeaderAddress());
				} catch (Exception e) {
					System.out.println("MS-DOS executables are not supported.");
					buf.seek(getPEHeaderAddress(buf));
				}

				// Verify PE signature ///////////

				if (!buf.match(PE_Header.PE_TAG)) {
					throw new BinaryParseException("PEModule: Missing PE signature");
				}
				// ////////////////////////////////
				COFF_Header coff = new COFF_Header(buf);
				// long optionalHeaderPos = inBuf.getCurrent();
				// long sectionPos = optionalHeaderPos +
				// coff_header.getSizeOfOptionalHeader();
				long posOptionalHeader = buf.getCurrent();
				PE_Header pe = new PE_Header(buf);
				long posSectionHeader = posOptionalHeader + coff.getSizeOfOptionalHeader();
				// /// Parse Section Headers and sections /////////////////////////
				// if (sectionPos != tempSectionPos)
				buf.seek(posSectionHeader);
				long alignment = pe.getFileAlignment();
				SectionHeader[] section = new SectionHeader[coff.getNumberOfSections()];
				for (int i = 0; i < coff.getNumberOfSections(); i++) {
					section[i] = new SectionHeader(buf, alignment);
				}
				// if (sectionPos != tempSectionPos)
				// inBuf.seek(tempSectionPos);
				// ///////////////////////////////////////////////////////////////

				long expTableRVA = pe.getDataDirectory()[ImageDataDirectory.EXPORT_TABLE_INDEX].VirtualAddress;
				if (expTableRVA > 0) { // We have an export table
					logger.debug("-- Reading export table...");
					buf.seek(getFilePointerRVA(expTableRVA, section));
					ImageExportDirectory imageExportDirectory = new ImageExportDirectory(buf);

					buf.seek(getFilePointerRVA(imageExportDirectory.AddressOfFunctions));
					// Parse EAT
					ExportEntry[] tmpEntries = new ExportEntry[(int) imageExportDirectory.NumberOfFunctions];
					int eatEntries = 0;
					for (int i = 0; i < tmpEntries.length; i++) {
						long rva = buf.readDWORD();
						if (rva > 0) {
							tmpEntries[i] = new ExportEntry((int) (i + imageExportDirectory.Base), new AbsoluteAddress(rva
									+ pe.getImageBase()));
							eatEntries++;
						}
					}

					long namePtr = getFilePointerRVA(imageExportDirectory.AddressOfNames,section);
					long ordPtr = getFilePointerRVA(imageExportDirectory.AddressOfNameOrdinals,section);
					for (int i = 0; i < imageExportDirectory.NumberOfNames; i++) {
						// read next ENT entry
						buf.seek(namePtr);
						long rva = buf.readDWORD();
						namePtr = buf.getCurrent();
						// read export name
						buf.seek(getFilePointerRVA(rva,section));
						String expName = buf.readASCII();
						// read next EOT entry
						buf.seek(ordPtr);
						int ord = buf.readWORD();
						ordPtr = buf.getCurrent();
						tmpEntries[ord].setName(expName);
						if (tmpEntries[ord].getOrdinal() == ordinal) {
							return expName;
						}
					}
					exportEntries = new ExportEntry[eatEntries];
					int j = 0;
					for (int i = 0; i < tmpEntries.length; i++) {
						if (tmpEntries[i] != null) {
							exportEntries[j++] = tmpEntries[i];
						}
					}
					logger.debug("-- Got " + exportEntries.length + " exported symbols.");
				} else {
					logger.debug("-- File contains no exports");
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BinaryParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return null;
	}
	
	private long getFilePointerRVA(long rva, SectionHeader[] section) {
		// TODO Auto-generated method stub
		int sct = getSectionNumberByRVA(rva, section);

		/*
		 * if
		 * (Program.getProgram().getFileName().equals("Flooder.Win32.AngryPing"
		 * )) return (rva - getSectionHeader(sct).VirtualAddress) +
		 * getSectionHeader(sct).PointerToRawData;
		 */
		if (sct < 0) {
//			return -1;
			return rva;
		}

		if (rva - section[sct].VirtualAddress > section[sct].SizeOfRawData) {
//			return -1;
			return rva;
		}
		return (rva - section[sct].VirtualAddress) + section[sct].PointerToRawData;
	}

	private int getSectionNumberByRVA(long rva, SectionHeader[] section) {
		// TODO Auto-generated method stub
		if (rva < 0) {
			return -1;
		}
		for (int i = 0; i < section.length; i++) {
			if (section[i].VirtualAddress <= rva
					&& (section[i].VirtualAddress + section[i].SizeOfRawData) > rva) {
				return i;
				/*
				 * if (fileName.equals("Flooder.Win32.AngryPing")) { for (int i
				 * = getNumberOfSections() - 1; i >=0; i--) if
				 * (getSectionHeader(i).VirtualAddress <= rva) return i; }
				 */
			}
		}

		return -1;
	}

	private long getFilePointerRVA(long rva) {
		// TODO Auto-generated method stub
		int sct = getSectionNumberByRVA(rva);

		/*
		 * if
		 * (Program.getProgram().getFileName().equals("Flooder.Win32.AngryPing"
		 * )) return (rva - getSectionHeader(sct).VirtualAddress) +
		 * getSectionHeader(sct).PointerToRawData;
		 */
		if (sct < 0) {
//			return -1;
			return rva;
		}

		if (rva - getSectionHeader(sct).VirtualAddress > getSectionHeader(sct).SizeOfRawData) {
//			return -1;
			return rva;
		}
		return (rva - getSectionHeader(sct).VirtualAddress) + getSectionHeader(sct).PointerToRawData;

	}

	private long getPEHeaderAddress(BinaryFileInputBuffer inBuf) {
		try {
			inBuf.seek(60);
			return inBuf.readDWORD();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return 0;
	}

	public Map<AbsoluteAddress, Pair<String, String>> getImportTable() {
		return importTable;
	}

	public int getNumberOfExports() {
		if (exportEntries != null) {
			return exportEntries.length;
		} else {
			return 0;
		}
	}

	public ExportEntry getExport(int num) {
		return exportEntries[num];
	}
	
	public ExportEntry getExportOrdinal(int ordinal) {
		for (ExportEntry e: exportEntries) {
			if (e.getOrdinal() == ordinal) {
				return e;
			}
		}
		return null;
	}

	@Override
	public AbsoluteAddress getEntryPoint() {
		return new AbsoluteAddress(getBaseAddress() + pe_header.getAddressOfEntryPoint());
	}

	@Override
	public boolean isReadOnly(AbsoluteAddress a) {
		int section = getSectionNumber(a);
		if (section >= 0) {
			return getSectionHeader(section).isReadOnlySection();
		} else {
			return false;
		}
	}

	public String getFileName() {
		return fileName;
	}

	@Override
	public SymbolFinder getSymbolFinder() {
		return symbolFinder;
	}

	@Override
	protected final long getBaseAddress() {
		return pe_header.getImageBase();
	}

	public long getImageBase() {
		return pe_header.getImageBase();
	}

	@Override
	protected final int getSectionNumberByRVA(long rva) {
		if (rva < 0) {
			return -1;
		}
		for (int i = 0; i < getNumberOfSections(); i++) {
			if (getSectionHeader(i).VirtualAddress <= rva
					&& (getSectionHeader(i).VirtualAddress + getSectionHeader(i).SizeOfRawData) > rva) {
				return i;
				/*
				 * if (fileName.equals("Flooder.Win32.AngryPing")) { for (int i
				 * = getNumberOfSections() - 1; i >=0; i--) if
				 * (getSectionHeader(i).VirtualAddress <= rva) return i; }
				 */
			}
		}

		return -1;
	}

	@Override
	public Set<UnresolvedSymbol> getUnresolvedSymbols() {
		Set<UnresolvedSymbol> unresolvedSymbols = new FastSet<UnresolvedSymbol>();
		for (Map.Entry<AbsoluteAddress, Pair<String, String>> importEntry : getImportTable().entrySet()) {
			AbsoluteAddress va = importEntry.getKey();
			String libraryName = importEntry.getValue().getLeft();
			String symbolName = importEntry.getValue().getRight();
			unresolvedSymbols.add(new UnresolvedSymbol(this, libraryName, symbolName, (int) getFilePointer(va),
					AddressingType.ABSOLUTE));
		}

		return unresolvedSymbols;
	}

	@Override
	public Set<ExportedSymbol> getExportedSymbols() {
		Set<ExportedSymbol> exportedSymbols = new FastSet<ExportedSymbol>();

		if (Options.wdm.getValue() && fileName.endsWith(".sys")) {
			// FIXME: adds multiple DriverEntries for multiple PE modules, and
			// is generally hackish
			logger.debug("Exporting DriverEntry at " + getEntryPoint());
			exportedSymbols.add(new ExportedSymbol(this, "_DriverEntry@8", getEntryPoint()));
		} else {
			exportedSymbols.add(new ExportedSymbol(this, "start", getEntryPoint()));
		}

		if (exportEntries != null) {
			for (ExportEntry ee : exportEntries) {
				String name = ee.getName();
				if (name == null) {
					name = "ord(" + ee.getOrdinal() + ")";
				}
				exportedSymbols.add(new ExportedSymbol(this, name, ee.getAddress()));
			}
		}
		return exportedSymbols;
	}

	@Override
	public boolean insideFileArea(AbsoluteAddress va) {
		// TODO Auto-generated method stub
		long t = va.getValue() - getBaseAddress();
		// return t >= 0 && t < pe_header.getSizeOfImage();
		return t > 0 && t < pe_header.getSizeOfImage();
	}
}
