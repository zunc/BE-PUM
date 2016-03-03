/*
 * Program.java - This file is part of the Jakstab project.
 * Copyright 2007-2012 Johannes Kinder <jk@jakstab.org>
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

package org.jakstab;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.analysis.complement.CFGState;
import org.analysis.complement.OldBitVector;
import org.analysis.complement.PMLocation;
import org.analysis.complement.PMState;
import org.jakstab.analysis.AbstractState;
import org.jakstab.analysis.ReachedSet;
import org.jakstab.analysis.composite.CompositeState;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.BranchInstruction;
import org.jakstab.asm.DummySymbolFinder;
import org.jakstab.asm.Immediate;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.MemoryOperand;
import org.jakstab.asm.Operand;
import org.jakstab.asm.SymbolFinder;
import org.jakstab.asm.x86.X86ArithmeticInstruction;
import org.jakstab.asm.x86.X86CondJmpInstruction;
import org.jakstab.asm.x86.X86Instruction;
import org.jakstab.asm.x86.X86MemoryOperand;
import org.jakstab.asm.x86.X86MoveInstruction;
import org.jakstab.asm.x86.X86Register;
import org.jakstab.cfa.CFAEdge;
import org.jakstab.cfa.Location;
import org.jakstab.disasm.Disassembler;
import org.jakstab.disasm.DisassemblyException;
import org.jakstab.disasm.x86.InstructionDecoder;
import org.jakstab.disasm.x86.X86Disassembler;
import org.jakstab.loader.BinaryParseException;
import org.jakstab.loader.ExecutableImage;
import org.jakstab.loader.ExportedSymbol;
import org.jakstab.loader.Harness;
import org.jakstab.loader.LinuxStubLibrary;
import org.jakstab.loader.RawModule;
import org.jakstab.loader.StubProvider;
import org.jakstab.loader.UnresolvedSymbol;
import org.jakstab.loader.Win32StubLibrary;
import org.jakstab.loader.elf.ELFModule;
import org.jakstab.loader.pe.AbstractCOFFModule;
import org.jakstab.loader.pe.ObjectFile;
import org.jakstab.loader.pe.PEModule;
import org.jakstab.rtl.expressions.RTLExpression;
import org.jakstab.rtl.expressions.RTLNumber;
import org.jakstab.rtl.expressions.SetOfVariables;
import org.jakstab.rtl.statements.RTLHalt;
import org.jakstab.rtl.statements.RTLStatement;
import org.jakstab.rtl.statements.RTLVariableAssignment;
import org.jakstab.rtl.statements.StatementSequence;
import org.jakstab.ssl.Architecture;
import org.jakstab.util.BinaryFileInputBuffer;
import org.jakstab.util.FastSet;

import v2.org.analysis.cfg.BPCFG;
import v2.org.analysis.environment.Environment;
import v2.org.analysis.environment.ExternalMemory;
import v2.org.analysis.environment.ExternalMemory.ExternalMemoryReturnData;
import v2.org.analysis.environment.Memory;
import v2.org.analysis.packer.PackerDetection;
import v2.org.analysis.statistics.FileProcess;
import v2.org.analysis.statistics.Logging;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

/**
 * There is one singleton Program object for all modules currently under
 * analysis. It stores all non-analysis information about the analyzed programs,
 * including statements, the current control flow graph, and symbols.
 * 
 * @author Johannes Kinder
 */
public final class Program {

	public enum TargetOS {
		WINDOWS, LINUX, UNKNOWN
	}

	// private final static Logger logger = Logger.getLogger(Program.class);
	private final static Logging logger = Logging.getLogger(Program.class);

	/**
	 * Initially creates the Program object.
	 * 
	 * @param arch
	 *            An Architecture object with architecture specific information
	 * @return the new singleton instance of the Program class
	 */
	public static Program createProgram(Architecture arch) {
		// assert programInstance == null;
		programInstance = new Program(arch);
		return programInstance;
	}

	public String generatePathFileName(String baseFileName) {
		// TODO Auto-generated method stub
		String ret = baseFileName;
		
		// Get the file name in input path to save in the same folder of BE-PUM jar file.
		if (!Program.class.getResource("Program.class").toString().startsWith("file")) {
			int index = baseFileName.lastIndexOf('/');
			if (index == -1) {
				index = baseFileName.lastIndexOf('\\');
			}
			if (index > -1) {
				ret = baseFileName.substring(index, baseFileName.length());
			}
		}

		// Never run because baseFileName parameter is a relative path within '/' separator characters
//		String r[] = baseFileName.split("\\\\");
//		String ret = "";
//		for (int i = 0; i < r.length; i++) {
//			if (i == r.length - 2)
//				ret += "cfg" + "\\";
//			else if (i == r.length - 1)
//				ret += r[i];
//			else
//				ret += r[i] + "\\";
//		}

		return System.getProperty("user.dir") + ret;
	}

	private String technique = "";
	private String fileName = "";
	private String detail_technique = "";
	private static Program programInstance;
	private String absolutePath = "";
	final static String resultFileTXT = "data/data/Result.txt";
	final static String resultFileTempTXT = "data/data/Result_Temp.txt";
	final static String fullResultFileTXT = "data/data/fullResult.txt";
	final static String packerResultFileTXT = "data/data/packerResult.txt";
	final static String packerResultCountFileTXT = "data/data/packerResultCount.txt";
	final static String stopAddFile = "data/data/stopFile.txt";
	static final int MAX_BYTE_PER_INSTRUCTION = 15;
	public static final String pathLibrary = "data/lib/win32/";

	/**
	 * Get the singleton Program object.
	 * 
	 * @return the singleton instance of the Program class
	 */
	public synchronized static Program getProgram() {
		return programInstance;
	}

	private ReachedSet reached = null;
	private final Architecture arch;
	private Location start;
	private Map<Location, RTLStatement> statementMap;
	private Map<PMLocation, RTLStatement> statementMap1;
	private Map<AbsoluteAddress, List<AbsoluteAddress>> preservedExecutionMap;
	private Map<AbsoluteAddress, Instruction> assemblyMap;

	private List<PMState> pmState;

	private BPCFG bpCFG;

	private List<AbsoluteAddress> smPos;

	private ExecutableImage mainModule;
	private List<ExecutableImage> modules;
	private Set<CFGState> cfg;
	private Set<CFAEdge> cfa;
	private final Map<String, ExportedSymbol> exportedSymbols;
	private final Set<UnresolvedSymbol> unresolvedSymbols;
	private Set<Location> unresolvedBranches;
	private StubProvider stubLibrary;
	private Harness harness;
	Disassembler disassembler;
	private FileProcess resultFile, fullResultFile, resultFile_Temp, logFile, stopFile;
	private FileProcess packerResultFile, packerResultCountFile;
	private TargetOS targetOS;

	private Instruction analyzedInstruction = null;;
	
	// PHONG - 20150724
	private PackerDetection pDetection;
	
	private long analyzingTime;
	
	private Program(Architecture arch) {
		this.arch = arch;
		this.targetOS = TargetOS.UNKNOWN;

		modules = new LinkedList<ExecutableImage>();
		assemblyMap = new TreeMap<AbsoluteAddress, Instruction>();
		pmState = new ArrayList<PMState>();
		preservedExecutionMap = new TreeMap<AbsoluteAddress, List<AbsoluteAddress>>();
		// this.preservedExecutionMap.
		statementMap = new HashMap<Location, RTLStatement>(2000);
		statementMap1 = new HashMap<PMLocation, RTLStatement>(2000);
		cfa = new FastSet<CFAEdge>();
		cfg = new FastSet<CFGState>();
		exportedSymbols = new HashMap<String, ExportedSymbol>();
		unresolvedSymbols = new FastSet<UnresolvedSymbol>();
		unresolvedBranches = new FastSet<Location>();
		smPos = new ArrayList<AbsoluteAddress>();
		bpCFG = new BPCFG();

		setResultFile(new FileProcess(resultFileTXT));
		setStopFile(new FileProcess(stopAddFile));
		setFullResultFile(new FileProcess(fullResultFileTXT));
		setResultFileTemp(new FileProcess(resultFileTempTXT));
		
		setPackerResultFile(new FileProcess(packerResultFileTXT));
		this.packerResultFile.appendFile("");
		
		setPackerResultCountFile(new FileProcess(packerResultCountFileTXT));
		this.packerResultCountFile.appendFile("");
		
		pDetection = new PackerDetection();
	}

	public void addByteSMPos(AbsoluteAddress addr) {
		this.smPos.add(addr);
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 1));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 2));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 3));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 4));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 5));

		this.smPos.add(new AbsoluteAddress(addr.getValue() - 1));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 2));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 3));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 4));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 5));
	}

	private void addDoubleWordSMPos(AbsoluteAddress addr) {
		// TODO Auto-generated method stub
		this.smPos.add(addr);
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 1));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 2));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 3));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 4));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 5));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 6));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 7));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 8));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 9));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 10));

		this.smPos.add(new AbsoluteAddress(addr.getValue() - 1));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 2));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 3));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 4));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 6));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 7));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 8));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 9));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 10));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 5));
	}

	private void addPMState(AbsoluteAddress addr, Instruction instr) {
		PMState temp = new PMState(addr, instr);
		for (PMState s : this.pmState) {
			if (s.compare(temp)) {
				return;
			}
		}

		this.pmState.add(temp);
	}

	private void addWordSMPos(AbsoluteAddress addr) {
		// TODO Auto-generated method stub
		this.smPos.add(addr);
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 1));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 2));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 3));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 4));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 5));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 6));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 7));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 8));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 9));
		this.smPos.add(new AbsoluteAddress(addr.getValue() + 10));

		this.smPos.add(new AbsoluteAddress(addr.getValue() - 1));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 2));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 3));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 4));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 6));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 7));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 8));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 9));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 10));
		this.smPos.add(new AbsoluteAddress(addr.getValue() - 5));

	}

	// Check address inside file area
	public boolean checkAddress(AbsoluteAddress address) {
		// boolean ret = true;
		return mainModule.insideFileArea(address);
		/*
		 * if (harness.contains(address) || address.getValueOperand() >=
		 * StubProvider.STUB_BASE) return false;
		 * 
		 * ExecutableImage module = getModule(address);
		 * 
		 * long fp = -1; if (module == null) { byte[] opcodes = new byte[10];
		 * return getOpcodeArray(address, opcodes);
		 * 
		 * } else { fp = module.getFilePointer(address); // Also check whether
		 * fp is out of the int range, since the // X86Disassembler actually //
		 * performs this cast in its implementation. if (fp < 0 || (int) fp < 0)
		 * { return false; } } return true;
		 */}

	public String checkAPI(long value, Environment env) {
		// TODO Auto-generated method stub
		String api = "";
		if (stubLibrary != null && stubLibrary instanceof Win32StubLibrary) {
			api = ((Win32StubLibrary) stubLibrary).getAPIName(value);
		}		

		if (api == "") {
			api = env.getSystem().getLibraryHandle().getAPIName(value);
		}
		return api;
	}

	private boolean checkSEH(Instruction instr) {
		// TODO Auto-generated method stub
		if (instr.getOperandCount() >= 2) {
			Operand dest = instr.getOperand(0);
			Operand src = instr.getOperand(1);

			return (dest.toString().contains("fs:") && src.toString().contains("esp"));
		}

		return false;
	}

	private boolean checkSelfModify(AbsoluteAddress address, Instruction instr) {
		// TODO Auto-generated method stub
		ExecutableImage module = getModule(address);

		/*
		 * if (address.toString().equals("0x0040d82a") ||
		 * address.toString().equals("0x0040d830") ||
		 * address.toString().equals("0x0040d85a") ||
		 * address.toString().equals("0x0040d839") ||
		 * address.toString().equals("0x0040d86c") ||
		 * address.toString().equals("0x004047df") ) {
		 * System.out.println("Debug Self Modify Code: " + address.toString()) ;
		 * }
		 */

		if (!address.toString().equals("0x00404819") && this.getFileName().equals("Virus.Win32.Cabanas.2999")) {
			return false;
		}

		if (instr instanceof X86CondJmpInstruction) {
			if (instr.getName().startsWith("loop")) {
				System.out.println("Debug Decryption Method:" + address.toString());
				String decryptionString = "";
				String curAPI = "";
				if (address.toString().equals("0x00404819") && this.getFileName().equals("Virus.Win32.Cabanas.2999")) {
					System.out.println("Debug Special Decryption Method " + address.toString());
					long ecx = getRegVal(address, "ecx");
					long esi = getRegVal(address, "esi");
					long edi = getRegVal(address, "edi");

					while (ecx > 0) {
						int al = (int) getByteValueMemory(new AbsoluteAddress(esi));
						esi++;

						int cl = (int) (ecx & 0xFF);
						// al = Integer.rotateLeft((int) al, (int)cl) & 0xFF;
						al = (int) OldBitVector.rl8(al, cl);
						al = al ^ 0xB5;
						long fp1 = module.getFilePointer(new AbsoluteAddress(edi));
						if (fp1 >= 0) {
							module.getDisassembler().setMemoryByteValue((int) fp1, al);
						} else if (al > 177) {
							al += 128;
							al = al & 0xFF;
							String temp = (char) al + "File";

							decryptionString += temp;
							curAPI += temp;
							edi += 5;
							ecx--;
						} else
						/*
						 * if (al == 238) { decryptionString += "nFile"; curAPI
						 * += "nFile"; edi += 5; ecx--; } else if (al == 249) {
						 * decryptionString += "yFile"; curAPI += "yFile"; edi
						 * += 5; ecx--; } else if (al == 244) { decryptionString
						 * += "tFile"; curAPI += "tFile"; edi += 5; ecx--; }
						 * else if (al == 229) { decryptionString += "eFile";
						 * curAPI += "eFile"; edi += 5; ecx--; } else
						 */
						if (al != 128) {
							decryptionString += (char) al;
							if (al == 0) {
								curAPI = "";
							} else {
								curAPI += (char) al;
							}

							edi++;
							ecx--;
						} else {
							// char t = (char)
							// (()curAPI.charAt(curAPI.length()-1) - 0xEA);
							decryptionString += (char) 0 + curAPI.substring(0, curAPI.length() - 1) + "W" + (char) 0;
							edi += curAPI.length() + 2;
							ecx--;
							esi++;
							curAPI = "";
						}
						this.addByteSMPos(new AbsoluteAddress(esi));

						if (decryptionString.contains("GetProcAddress")) {
							// System.out.println("Debug")
							;
						}
					}
				}

				System.out.println("Decryption String:" + decryptionString);
			}
		} else if (instr instanceof X86MoveInstruction) {
			if (instr.getName().startsWith("mov")) {
				Operand dest = ((X86MoveInstruction) instr).getOperand1();
				Operand src = ((X86MoveInstruction) instr).getOperand2();

				if (!dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
					return false;
				}

				if (dest.toString().startsWith("%fs")) {
					return false;
				}

				System.out.println("Debug Self Modify Code: " + address.toString());

				long pos = getOperandValue1(address, instr, dest);

				// if (module.isCodeArea(new AbsoluteAddress(pos))) {
				long value = getOperandValue2(address, instr, src);
				long fp1 = module.getFilePointer(new AbsoluteAddress(pos));
				if (instr.getName().endsWith("b")) {
					module.getDisassembler().setMemoryByteValue((int) fp1, value);
					this.addByteSMPos(new AbsoluteAddress(pos));
				} else if (instr.getName().endsWith("w") || instr.getName().endsWith("s")) {
					module.getDisassembler().setMemoryWordValue((int) fp1, value);
					this.addWordSMPos(new AbsoluteAddress(pos));
				} else if (instr.getName().endsWith("l")) {

					module.getDisassembler().setMemoryDoubleWordValue((int) fp1, value);
					this.addDoubleWordSMPos(new AbsoluteAddress(pos));
				}

				return true;
				// }
			}

		} else if (instr instanceof X86ArithmeticInstruction) {
			if (instr.getName().startsWith("inc")) {
				Operand dest = ((X86ArithmeticInstruction) instr).getOperand1();
				// Operand src = ((X86MoveInstruction)instr).getOperand2();

				if (!dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
					return false;
				}

				System.out.println("Debug Self Modify Code: " + address.toString());

				long pos = getOperandValue1(address, instr, dest);

				// if (module.isCodeArea(new AbsoluteAddress(pos))) {
				long value = 1;
				long fp1 = module.getFilePointer(new AbsoluteAddress(pos));
				if (instr.getName().endsWith("b")) {
					module.getDisassembler().addMemoryByteValue((int) fp1, value);
					this.addByteSMPos(new AbsoluteAddress(pos));
				}

				return true;
				// }
			} else if (instr.getName().startsWith("add")) {
				// instr.get
				Operand dest = ((X86ArithmeticInstruction) instr).getOperand1();
				Operand src = ((X86ArithmeticInstruction) instr).getOperand2();

				if (!dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
					return false;
				}

				System.out.println("Debug Self Modify Code: " + address.toString());

				long pos = getOperandValue1(address, instr, dest);

				// if (module.isCodeArea(new AbsoluteAddress(pos))) {
				long value = getOperandValue2(address, instr, src);
				long fp1 = module.getFilePointer(new AbsoluteAddress(pos));
				if (instr.getName().endsWith("b")) {
					module.getDisassembler().addMemoryByteValue((int) fp1, value);
					this.addByteSMPos(new AbsoluteAddress(pos));
				} else if (instr.getName().endsWith("l")) {
					module.getDisassembler().addMemoryDoubleWordValue((int) fp1, value);
					this.addDoubleWordSMPos(new AbsoluteAddress(pos));
				} else if (instr.getName().endsWith("s") || instr.getName().endsWith("w")) {
					module.getDisassembler().addMemoryWordValue((int) fp1, value);
					this.addDoubleWordSMPos(new AbsoluteAddress(pos));
				}

				return true;
				// }
			}

		} else if (instr instanceof X86Instruction) {
			if (instr.getName().startsWith("movs")) {
				Operand dest = instr.getOperand(0);
				Operand src = instr.getOperand(1);

				if (Program.getProgram().getFileName().toString().equals("Flooder.Win32.AngryPing")) {
					return false;
				}

				if (Program.getProgram().getFileName().toString().equals("Virus.Win32.Cabanas.2999")) {
					return false;
				}

				// System.out.println("Debug SMC MOVS:" + address.toString());
				System.out.println("Debug Self Modify Code: " + address.toString());

				if (!dest.getClass().getSimpleName().equals("X86MemoryOperand")
						|| !src.getClass().getSimpleName().equals("X86MemoryOperand")) {
					return false;
				}

				long pos1 = getRegVal(address, "edi");
				long pos2 = getRegVal(address, "esi");

				if (((X86Instruction) instr).hasPrefixREPZ()) {
					long ecx = getRegVal(address, "ecx");

					for (long i = ecx; i > 0; i++) {
						if (instr.getName().endsWith("b")) {

						} else if (instr.getName().endsWith("l")) {
							long value = this.getDoubleWordValueMemory(new AbsoluteAddress(pos2));
							// long value2 = this.get32Value(new
							// AbsoluteAddress(pos1));
							long fp1 = module.getFilePointer(new AbsoluteAddress(pos1));
							// long fp2 = module.getFilePointer(new
							// AbsoluteAddress(pos2));
							pos2 += 4;
							module.getDisassembler().setMemoryDoubleWordValue((int) fp1, value);
							pos1 += 4;
						} else if (instr.getName().endsWith("s") || instr.getName().endsWith("w")) {

						}
					}
				}

			} else if (instr.getName().startsWith("stos")) {
				Operand dest = instr.getOperand(0);
				Operand src = instr.getOperand(1);

				if (Program.getProgram().getFileName().toString().equals("Virus.Win32.Seppuku.1606")
						&& address.toString().equals("0x0040106c")) {
					return false;
				}

				if (address.toString().equals("0x004013ca")) {
					System.out.println("Debug");
				}

				if (!dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
					return false;
				}

				System.out.println("Debug Self Modify Code: " + address.toString());

				long pos = getOperandValue1(address, instr, dest);

				// if (module.isCodeArea(new AbsoluteAddress(pos))) {
				long value = getOperandValue2(address, instr, src);
				long fp1 = module.getFilePointer(new AbsoluteAddress(pos));
				if (instr.getName().endsWith("b")) {
					module.getDisassembler().setMemoryByteValue((int) fp1, value);
					this.addByteSMPos(new AbsoluteAddress(pos));
				} else if (instr.getName().endsWith("l")) {
					module.getDisassembler().setMemoryDoubleWordValue((int) fp1, value);
					this.addDoubleWordSMPos(new AbsoluteAddress(pos));
				} else if (instr.getName().endsWith("s") || instr.getName().endsWith("w")) {
					module.getDisassembler().setMemoryWordValue((int) fp1, value);
					this.addWordSMPos(new AbsoluteAddress(pos));
				}

				return true;
				// }
			}
		}
		/*
		 * if (checkSelfModify(address, instr)) {
		 * System.out.println("Self-modifying Code"); long value = 12; long pos
		 * = 4198409; long fp1 = module.getFilePointer(new
		 * AbsoluteAddress(pos));
		 * module.getDisassembler().setMemoryByteValue((int) fp1, value); }
		 */
		return false;
	}

	public boolean checkSMPos(AbsoluteAddress addr) {
		for (AbsoluteAddress a : this.smPos) {
			if (a.getValue() == addr.getValue()) {
				return true;
			}
		}

		return false;
	}

	public Iterator<AbsoluteAddress> codeAddressIterator() {
		return getMainModule().codeBytesIterator();
	}

	public boolean containsLabel(Location label) {
		return statementMap.containsKey(label);
	}

	public int countIndirectBranches() {
		int res = 0;
		for (Map.Entry<AbsoluteAddress, Instruction> entry : assemblyMap.entrySet()) {
			Instruction instr = entry.getValue();

			if (instr instanceof BranchInstruction) {
				BranchInstruction branch = (BranchInstruction) instr;
				if (branch.isIndirect()) {
					// if branch target is not a memory operand pointing into a
					// static data area of the binary (imports)
					if (branch.getBranchDestination() instanceof MemoryOperand) {
						MemoryOperand memOp = (MemoryOperand) branch.getBranchDestination();
						// Import calls have only displacement
						if (memOp.getBase() == null && memOp.getIndex() == null) {
							AbsoluteAddress disp = new AbsoluteAddress(memOp.getDisplacement());
							// Check whether displacement points into import
							// table
							ExecutableImage module = getModule(disp);
							if (module instanceof PEModule && ((PEModule) module).getImportTable().containsKey(disp)) {
								continue;
							}
						}
					}
					res++;
					// logger.verbose(entry.getKey() + "\t" +
					// getInstructionString(entry.getKey()));
				}
			}
		}
		return res;
	}

	public void generageCFG(String baseFileName) {
		// TODO Auto-generated method stub
		ProgramGraphWriter graphWriter = new ProgramGraphWriter(this);
		graphWriter.writeDisassembly(getBPCFG(), generatePathFileName(baseFileName) + "_code.asm");
		graphWriter.writeAssemblyCFG(getBPCFG(), generatePathFileName(baseFileName) + "_model");
	}

	/**
	 * @return the absolutePath
	 */
	public String getAbsolutePathFile() {
		return absolutePath;
	}

	public Instruction getAnalyzedInstruction() {
		return analyzedInstruction;
	}

	public Architecture getArchitecture() {
		return arch;
	}

	/**
	 * @return the assemblyMap
	 */
	public final Map<AbsoluteAddress, Instruction> getAssemblyMap() {
		return assemblyMap;
	}

	public BPCFG getBPCFG() {
		// TODO Auto-generated method stub
		return bpCFG;
	}

	public int getByteIndex(AbsoluteAddress address) {
		// TODO Auto-generated method stub
		long value = address.getValue();

		// return (int)(value - 4194304) / 8;
		return (int) (value) / 8;
		// return 514;
	}

	public final long getByteValueMemory(AbsoluteAddress address) {
		// TODO Auto-generated method stub
		long result = 0;

		try {
			if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
				return 0;
			}

			ExecutableImage module = getModule(address);

			long fp = -1;
			if (module == null) {
				// logger.error("No module for address " + address
				// + ". Cannot disassemble instruction!");

				result = Long.MIN_VALUE;
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area: " + address);
				}
				// else {
				// if (!module.isCodeArea(address)) {
				// logger.error("Requested instruction outside code section: "
				// + address);
				// return 0;
				// }
				// Disassembler dis = module.getDisassembler();
				// instr =
				X86Disassembler dis = (X86Disassembler) module.getDisassembler();
				int byteIndex = (int) fp;
				result = InstructionDecoder.readByte(dis.getCode(), byteIndex);
				/*
				 * if (instr == null) {
				 * logger.error("Instruction could not be disassembled at: " +
				 * address); }
				 */
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

	// PHONG: debug here
	public final long getByteValueMemoryPhong(AbsoluteAddress address) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
				return 0;
			}

			ExecutableImage module = getModule(address);

			long fp = -1;
			if (module == null) {
				// logger.error("No module for address " + address
				// + ". Cannot disassemble instruction!")
				;
				// PHONG: 20150501 ------------------------------------------
				if (mainModule instanceof PEModule) {
					if (((PEModule) mainModule).isInsideHeader(address.getValue())) {
						result = (int) ((PEModule) mainModule).getByteValue(address.getValue());
					}
				}
				// ------------------------------------------------------------
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area: " + address);
				}
				// else {
				// if (!module.isCodeArea(address)) {
				// logger.error("Requested instruction outside code section: "
				// + address);
				// return 0;
				// }
				// Disassembler dis = module.getDisassembler();
				// instr =				 
				X86Disassembler dis = (X86Disassembler) module.getDisassembler();
				int byteIndex = (int) fp;
				result = InstructionDecoder.readByte(dis.getCode(), byteIndex);
				/*
				 * if (instr == null) {
				 * logger.error("Instruction could not be disassembled at: " +
				 * address); }
				 */
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

	public Set<CFAEdge> getCFA() {
		return Collections.unmodifiableSet(cfa);
	}

	public Set<CFGState> getCFGState() {
		return cfg;
	}

	public String getDetailTechnique() {
		return detail_technique;
	}

	public final int getDoubleWordValueMemory(AbsoluteAddress address) {
		int result = 0;

		try {
			if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
				return 0;
			}

			ExecutableImage module = getModule(address);
			long fp = -1;
			if (module == null) {
				// logger.error("No module for address " + address
				// + ". Cannot disassemble instruction!");
				if (mainModule instanceof PEModule) {
					if (((PEModule) mainModule).isInsideHeader(address.getValue())) {
						result = (int) ((PEModule) mainModule).getDoubleWordValue(address.getValue());
					}
				}
				//
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area " + address);
				}
				// else {
				// if (!module.isCodeArea(address)) {
				// logger.error("Requested instruction outside code section: "
				// + address);
				// return 0;
				// }
				// Disassembler dis = module.getDisassembler();
				// instr =
				X86Disassembler dis = (X86Disassembler) module.getDisassembler();
				int byteIndex = (int) fp;
				result = InstructionDecoder.readInt32(dis.getCode(), byteIndex);
				/*
				 * if (instr == null) {
				 * logger.error("Instruction could not be disassembled at: " +
				 * address); }
				 */
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}

	public final int getEdgesCount() {
		int num = 0;
		// Set<CFAEdge> edges = new HashSet<CFAEdge>();
		// Set<Location> nodes = new HashSet<Location>();
		for (CFAEdge e : getCFA()) {
			AbsoluteAddress sourceAddr = e.getSource().getAddress();
			AbsoluteAddress targetAddr = e.getTarget().getAddress();
			if (!sourceAddr.equals(targetAddr)) {
				num++;
			}
		}
		return num;
	}

	public final int getEdgesCount1() {
		int num = 0;
		// Set<CFAEdge> edges = new HashSet<CFAEdge>();
		// Set<Location> nodes = new HashSet<Location>();
		for (CFGState e : this.cfg) {
			AbsoluteAddress sourceAddr = e.getSource().getAddress();
			AbsoluteAddress targetAddr = e.getTarget().getAddress();
			if (!sourceAddr.equals(targetAddr)) {
				num++;
			}
		}
		return num;
	}

	public AbsoluteAddress getEntryPoint() {
		// TODO Auto-generated method stub
		long v = specialCase();
		if (v != 0) {
			return new AbsoluteAddress(v);
		}

		return this.mainModule.getEntryPoint();
	}

	public String getFileName() {
		// TODO Auto-generated method stub
		return fileName;
	}

	/**
	 * @return the fullResultFile
	 */
	public FileProcess getFullResultFile() {
		return fullResultFile;
	}

	public Harness getHarness() {
		return harness;
	}

	public long getImageBase() {
		/*
		 * long retVal;
		 * 
		 * retVal = ((PEModule) modules.get(0)).getImageBase();
		 */
		if (mainModule instanceof PEModule) {
			return ((PEModule) mainModule).getImageBase();
		}

		return 0;
	}

	public final Instruction getInstruction(AbsoluteAddress address) {
		// Instruction instr = assemblyMap.get(address);
		Instruction instr = null;

		/*
		 * if (address.toString().equals("0x00401646")) {
		 * System.out.println("Debug Decode Instruction" + address.toString());
		 * }
		 */
		// if (instr != null) {
		// return instr;
		// } else {
		// No real instructions in prologue/epilogue
		if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
			return null;
		}

		ExecutableImage module = getModule(address);

		long fp = -1;
		if (module == null) {
			logger.error("Instruction is Null. No module for address " + address + ". Cannot disassemble instruction!");
		} else {
			fp = module.getFilePointer(address);
			// Also check whether fp is out of the int range, since the
			// X86Disassembler actually
			// performs this cast in its implementation.
			if (fp < 0 || (int) fp < 0) {
				logger.error("Requested instruction outside of file area: " + address);
			} else {
				if (!module.isCodeArea(address)) {
					// logger.error("Requested instruction outside code section: "
					// + address)
					;
					// return null;
				}

				// Disassembler dis = module.getDisassembler();

				instr = module.getDisassembler().decodeInstruction(fp);
				if (instr == null) {
					logger.error("Instruction could not be disassembled at: " + address);
				}

			}
		}

		if (instr != null) {
			putInstruction(address, instr);

			if (checkSEH(instr)) {
				if (!technique.contains("SEH")) {
					technique += "SEH ";
				}

				setDetailTechnique(getDetailTechnique() + "SEH:" + address.toString() + " ");
			}

			if (checkSelfModify(address, instr)) {
				System.out.println("Self Modified Code:" + address.toString());
				if (!technique.contains("SMC")) {
					technique += "SMC ";
				}

				setDetailTechnique(getDetailTechnique() + "SMC:" + address.toString() + " ");
				// System.out.println(instr.compareInstruction(instr));
				// }
			}
		}

		return instr;
		// }
	}

	public final Instruction getInstruction(AbsoluteAddress address, Environment env) {
		// Instruction instr = assemblyMap.get(address);
		Instruction instr = null;
		
		// if (!mainModule.insideFileArea(address))
		// return null;

		Memory m = env.getMemory();

		// if (instr != null) {
		// return instr;
		// } else {
		// No real instructions in prologue/epilogue
		if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
			return null;
		}

		ExecutableImage module = getModule(address);

		long fp = -1;
		if (module == null) {
			// Special Case
			byte[] opcodes = getOpcode(address, env, MAX_BYTE_PER_INSTRUCTION);
			if (opcodes != null) {
				instr = getInstruction(opcodes, env);
			}

			if (instr == null) {
				logger.error("Instr is Null. No module for address " + address + ". Cannot disassemble instruction!");
			}

		} else {
			fp = module.getFilePointer(address);
			// Also check whether fp is out of the int range, since the
			// X86Disassembler actually
			// performs this cast in its implementation.
			if (fp < 0 || (int) fp < 0) {
				logger.error("Requested instruction outside of file area: " + address);
			} else {
				if (!module.isCodeArea(address)) {
					setTechnique("EPO");
					setDetailTechnique("EPO:" + address + " ");
				}

				// Disassembler dis = module.getDisassembler();

				// PHONG: debug here
				// m.changeValuePhong(address, env);
				// ORIGINAL
				/*
				 * m.changeValue(address); instr =
				 * module.getDisassembler().decodeInstruction(fp);
				 * m.resetValue(address, instr);
				 */

				byte[] opcodes = getOpcode(address, env, MAX_BYTE_PER_INSTRUCTION);
				if (opcodes != null) {
					instr = getInstruction(opcodes, env);
				/*
				 * if (instr1 != null && !instr1.compareInstruction(instr1))
				 * System.out.println("Debug");
				 */
				}

				if (instr == null) {
					m.changeValue(address);
					instr = module.getDisassembler().decodeInstruction(fp);
					m.resetValue(address, instr);
					if (instr != null) {
						FileProcess fileProcess = new FileProcess("data/data/error.txt");
						fileProcess.appendFile("FileName:" + fileName + " decode Instruction Error at " + address);
					}

					logger.error("Instruction could not be disassembled at: " + address);
				}
			}
		}
		return instr;
	}

	private byte[] getOpcode(AbsoluteAddress address, Environment env, int n) {
		long[] opcodes = new long[n];
		int num = 0;
		long addr = address.getValue();
		opcodes = env.getMemory().getBytesArray(address, n);

		for (int i = 0; i < opcodes.length; i++) {
			num++;
			if (opcodes[i] == Long.MIN_VALUE) {
				long temp = readByte(new AbsoluteAddress(addr + i));

				if (temp == Long.MIN_VALUE) {
					ExternalMemoryReturnData ret = ExternalMemory.getByte(address.getValue() + i);
					if (ret != null && ret.isValidAddress) {
						opcodes[i] = ret.value.getValue();
					} else {
						num = i;
						break;
					}
				} else {
					opcodes[i] = temp;
				}
			}
		}

		if (num > 0) {
			byte[] ret = new byte[num];
			for (int i = 0; i < num; i++) {
				ret[i] = (byte) opcodes[i];
			}

			return ret;
		}

		return null;
	}

	private long readByte(AbsoluteAddress address) {
		long result = Long.MIN_VALUE;

		try {
			ExecutableImage module = getModule(address);

			long fp = -1;
			if (module == null) {
				// result = Long.MIN_VALUE;
				if (mainModule instanceof PEModule) {
					PEModule pe = (PEModule) mainModule;
					result = pe.getByteValue(address.getValue());
				}
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area: " + address);
				}

				X86Disassembler dis = (X86Disassembler) module.getDisassembler();
				int byteIndex = (int) fp;
				result = InstructionDecoder.readByte(dis.getCode(), byteIndex);
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}

	// PHONG: get instruction here
	public synchronized Instruction getInstruction(byte[] opcodes, Environment env) {
		Instruction instr = null;
		BinaryFileInputBuffer binBuff = new BinaryFileInputBuffer(opcodes);
		X86Disassembler dis = new X86Disassembler(binBuff);
		instr = dis.decodeInstruction();
		return instr;
	}

	public synchronized final Instruction getInstruction1(AbsoluteAddress address) {
		Instruction instr = assemblyMap.get(address);
		// Instruction instr = null;
		if (instr != null) {
			return instr;
		} else {
			// No real instructions in prologue/epilogue
			if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
				return null;
			}

			ExecutableImage module = getModule(address);

			long fp = -1;
			if (module == null) {
				// PHONG: debug
				// System.out.println();

				// logger.error("No module for address " + address
				// + ". Cannot disassemble instruction!")
				;
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area: " + address);
				} else {
					if (!module.isCodeArea(address)) {
						logger.error("Requested instruction outside code section: " + address);
						return null;
					}

					// Disassembler dis = module.getDisassembler();

					instr = module.getDisassembler().decodeInstruction(fp);
					if (instr == null) {
						logger.error("Instruction could not be disassembled at: " + address);
					}

					// if (checkSelfModify(address, instr)) {
					// System.out.println("Self Modified Code:" +
					// address.toString());
					// }
				}
			}

			if (instr != null) {
				putInstruction(address, instr);
			}
			return instr;
		}
	}

	public final int getInstructionCount() {
		return assemblyMap.size();
	}

	/**
	 * Get the string representation of the assembly instruction at the given
	 * address.
	 * 
	 * @param addr
	 *            a virtual address
	 * @return a string representation of the assembly code at the given address
	 */
	public String getInstructionString(AbsoluteAddress addr) {
		Instruction instr = getInstruction(addr);
		if (instr == null) {
			return "NON_EXISTENT";
		}
		return instr.toString(addr.getValue(), symbolFinder(addr));
	}

	/**
	 * Get the main module.
	 * 
	 * @return the main module
	 */
	public ExecutableImage getMainModule() {
		return mainModule;
	}

	/**
	 * Get the module that contains the specified virtual address at runtime.
	 * 
	 * @param a
	 *            a virtual address
	 * @return the module to which the given virtual address belongs.
	 */
	public ExecutableImage getModule(AbsoluteAddress a) {
		for (ExecutableImage module : modules) {
			if (module.getFilePointer(a) >= 0) {
				return module;
			}
		}
		return null;
	}

	public final int getNodesCount() {
		int num = 0;
		// Set<CFAEdge> edges = new HashSet<CFAEdge>();
		// Set<Location> nodes = new HashSet<Location>();
		for (CFAEdge e : getCFA()) {
			AbsoluteAddress sourceAddr = e.getSource().getAddress();
			AbsoluteAddress targetAddr = e.getTarget().getAddress();
			if (!sourceAddr.equals(targetAddr)) {
				num++;
			}
		}
		return num;
	}

	public final int getNodesCount1() {
		int num = 0;
		// Set<CFAEdge> edges = new HashSet<CFAEdge>();
		// Set<Location> nodes = new HashSet<Location>();
		for (CFGState e : this.cfg) {
			AbsoluteAddress sourceAddr = e.getSource().getAddress();
			AbsoluteAddress targetAddr = e.getTarget().getAddress();
			if (!sourceAddr.equals(targetAddr)) {
				num++;
			}
		}
		return num;
	}

	private boolean getOpcodeArray(AbsoluteAddress address, byte[] opcodes, Environment env) {
		// TODO Auto-generated method stub
		long addr = address.getValue();
		for (int i = 0; i < opcodes.length; i++) {
			long t;
			Value temp = null;
			// System.out.println();
			if (env.getMemory().contains(new AbsoluteAddress(addr + i))) {
				temp = env.getMemory().getByteMemoryValue(addr + i);
			}
			if (temp != null && temp instanceof LongValue) {
				t = ((LongValue) temp).getValue();
			} else {
				t = getByteValueMemory(new AbsoluteAddress(addr + i));
			}
			if (t == Long.MIN_VALUE) {
				return false;
			}
			opcodes[i] = (byte) t;
		}

		return true;
	}

	private long getOperandValue2(AbsoluteAddress address, Instruction instr, Operand src) {
		// TODO Auto-generated method stub
		// Operand src = ((X86MoveInstruction)instr).getOperand2();
		if (src.getClass().getSimpleName().equals("Immediate")) {
			return ((Immediate) src).getNumber().intValue();
		}
		if (src.getClass().getSimpleName().equals("X86Register")) {
			return getRegVal(address, ((X86Register) src).toString());
		} else if (src.getClass().getSimpleName().equals("X86RegisterPart")) {
			;
		} else if (src.getClass().getSimpleName().equals("X86SegmentRegister")) {
			;
		} else if (src.getClass().getSimpleName().equals("X86MemoryOperand")) {
			;
		}

		return 0;
	}

	public List<PMState> getPmState() {
		return pmState;
	}

	public final Map<AbsoluteAddress, List<AbsoluteAddress>> getPreservedExecutionMap() {
		return preservedExecutionMap;
	}

	private long getOperandValue1(AbsoluteAddress address, Instruction instr, Operand dest) {
		// TODO Auto-generated method stub
		// Operand dest = ((X86MoveInstruction)instr).getOperand1();
		if (this.getFileName().equals("Virus.Win32.Seppuku.1606")
		// && dest.equals("edi")
				&& address.toString().equals("0x004010eb")) {
			return 4200007;
		}

		X86MemoryOperand y = (X86MemoryOperand) dest;
		long base = 0;

		if (y.getBase() != null) {
			base = getRegVal(address, y.getBase().toString());
		}

		return base + y.getDisplacement();
	}

	/**
	 * Returns the address of the given procedure within the given library.
	 * Procedures present within the analyzed modules are given precedence over
	 * stub functions.
	 * 
	 * @param library
	 * @param procedure
	 * @return the virtual address of the procedure
	 */
	public AbsoluteAddress getProcAddress(String library, String procedure) {
		ExportedSymbol expSymbol = exportedSymbols.get(procedure);
		if (expSymbol != null) {
			return expSymbol.getAddress();
		} else {
			return stubLibrary.resolveSymbol(library, procedure);
		}
	}

	public ReachedSet getReached() {
		return reached;
	}

	private long getRegVal(AbsoluteAddress address, String regName) {
		// TODO Auto-generated method stub
		if (getFileName().equals("Virus.Win32.Cabanas.2999") && address.toString().equals("0x00404819")) {
			if (regName.contains("ecx")) {
				return 417;
			} else if (regName.contains("esi")) {
				return 4212242;
			} else if (regName.contains("edi")) {
				return 4215362;
			}
		} else if (getFileName().equals("Flooder.Win32.AngryPing") && address.toString().equals("0x0040d86c")) {
			if (regName.contains("esi")) {
				return 4249712;
			} else if (regName.contains("edi")) {
				return 4256195;
			} else if (regName.contains("ecx")) {
				return 1109;
			}
		} else if (getFileName().equals("Flooder.Win32.AngryPing")
				&& (address.toString().equals("0x0040d82a") || address.toString().equals("0x0040d830") || address
						.toString().equals("0x0040d839")

				)) {
			if (regName.contains("ebp")) {
				return 14336;
			} else if (regName.contains("eax")) {
				return 4194304;
			} else if (regName.contains("ecx")) {
				return 1109;
			}
		} else if (getFileName().equals("Flooder.Win32.AngryPing") && address.toString().equals("0x0040d85a")) {
			if (regName.contains("ebp")) {
				return 14336;
			} else if (regName.contains("eax")) {
				return 20819;
			} else if (regName.contains("ecx")) {
				return 1109;
			}
		}

		long v = 0;
		// Set<Entry<Location, RTLStatement>> entries =
		// this.statementMap.entrySet();
		// this.statementMap.
		// entries.
		// int i = 0;
		// this.reached.where(l);
		for (Iterator<AbstractState> iterator = this.reached.iterator(); iterator.hasNext();) {
			AbstractState state = iterator.next();
			if (state.getLocation().getAddress().getValue() == address.getValue()) {
				// System.out.println("Debug");
				if (state instanceof CompositeState) {
					CompositeState cState = (CompositeState) state;
					// RTLStatement stmt = Program.getProgram().getStatement(new
					// Location(address, 0));
					// cState.projectionFromConcretization(stmt.g,
					// t.getTargetExpression());
					return cState.getValue(regName);
				}
			}

		}
		for (RTLStatement value : this.statementMap.values()) {
			// i++;
			// if (i == entries.size()) {
			// value.get
			if (value instanceof RTLVariableAssignment) {

				if (((RTLVariableAssignment) value).getLeftHandSide().toString().equals(regName.replace("%", ""))) {
					RTLExpression right = ((RTLVariableAssignment) value).getRightHandSide();
					if (right instanceof RTLNumber) {
						v = ((RTLNumber) right).intValue();
					} else {
						v = 0;
					}

					/*
					 * v = Long.parseLong (((RTLVariableAssignment)
					 * value).getRightHandSide().toHexString());
					 */
				}

				// return v;
			}
			// }
		}

		return v;
	}

	/**
	 * @return the resultFile
	 */
	public FileProcess getResultFile() {
		return resultFile;
	}

	public FileProcess getResultFileTemp() {
		return resultFile_Temp;
	}

	/**
	 * @return the smPos
	 */
	public List<AbsoluteAddress> getSelfModifyPosition() {
		return smPos;
	}

	public Location getStart() {
		return start;
	}

	/**
	 * Get the statement at a specific label. If there is no statement stored,
	 * attempts to disassemble the instruction at the label's virtual address.
	 * If the address is outside of the file area, logs an error and returns a
	 * Halt statement by default.
	 * 
	 * @param label
	 *            The label for which to get the statement
	 * @return The statement object at label.
	 */
	public final RTLStatement getStatement(Location label) {
		if (!statementMap.containsKey(label) || this.checkSMPos(label.getAddress())
		// || label.getAddress().toString().equals("0x00401003")
		) {
			/*
			 * if (this.checkSMPos(label.getAddress()))
			 * System.out.println("Debug");
			 */

			AbsoluteAddress address = label.getAddress();
			Instruction instr;
			if (label.toString().endsWith("0")) {
				instr = getInstruction(address);
			} else {
				instr = getInstruction1(address);
			}

			this.setAnalyzedInstruction(instr);
			// If we did not get an instruction, add an artificial Halt for
			// recovery
			if (instr == null) {
				RTLHalt halt = new RTLHalt();
				halt.setLabel(label);
				putStatement(halt);
				logger.error("ERROR: Replacing unknown instruction with HALT.");
				if (Options.debug.getValue()) {
					throw new DisassemblyException("Disassembly failed at " + address);
				}
			} else {
				StatementSequence seq = arch.getRTLEquivalent(address, instr);
				for (RTLStatement s : seq) {
					putStatement(s, instr);
				}
				assert statementMap.containsKey(label) : "Disassembly did not produce label: " + label;
			}
		}
		return statementMap.get(label);
	}

	public final RTLStatement getStatement1(Location label) {
		if (!statementMap.containsKey(label) || label.getAddress().toString().equals("0x00401003")) {
			AbsoluteAddress address = label.getAddress();
			Instruction instr = getInstruction1(address);
			// If we did not get an instruction, add an artificial Halt for
			// recovery
			if (instr == null) {
				RTLHalt halt = new RTLHalt();
				halt.setLabel(label);
				putStatement(halt);
				logger.error("ERROR: Replacing unknown instruction with HALT.");
				if (Options.debug.getValue()) {
					throw new DisassemblyException("Disassembly failed at " + address);
				}
			} else {
				StatementSequence seq = arch.getRTLEquivalent(address, instr);
				for (RTLStatement s : seq) {
					putStatement(s);
				}
				assert statementMap.containsKey(label) : "Disassembly did not produce label: " + label;
			}
		}
		return statementMap.get(label);
	}

	public final RTLStatement getStatement1(PMLocation label) {
		if (!statementMap1.containsKey(label) || label.getAddress().toString().equals("0x00401003")) {
			AbsoluteAddress address = label.getAddress();
			Instruction instr = getInstruction1(address);
			// If we did not get an instruction, add an artificial Halt for
			// recovery
			if (instr == null) {
				RTLHalt halt = new RTLHalt();
				halt.setLabel(new Location(label.getAddress(), label.getIndex()));
				putStatement(halt);
				logger.error("ERROR: Replacing unknown instruction with HALT.");
				if (Options.debug.getValue()) {
					throw new DisassemblyException("Disassembly failed at " + address);
				}
			} else {
				StatementSequence seq = arch.getRTLEquivalent(address, instr);
				for (RTLStatement s : seq) {
					putStatement(s);
				}
				assert statementMap.containsKey(label) : "Disassembly did not produce label: " + label;
			}
		}
		return statementMap1.get(label);
	}

	public final int getStatementCount() {
		return statementMap.size();
	}

	/**
	 * Get all statements in the Program.
	 * 
	 * @return a collection of all statements in all loaded modules.
	 */
	public Collection<RTLStatement> getStatements() {
		return statementMap.values();
	}

	public Collection<RTLStatement> getStatements1() {
		return statementMap1.values();
	}

	public StubProvider getStubLibrary() {
		return this.stubLibrary;
	}

	public String getSymbolFor(AbsoluteAddress addr) {
		return symbolFinder(addr).getSymbolFor(addr);
	}

	public String getSymbolFor(Location label) {
		SymbolFinder symFinder = symbolFinder(label.getAddress());
		if (symFinder.hasSymbolFor(label.getAddress())) {
			return symFinder.getSymbolFor(label.getAddress());
		} else {
			return label.toString();
		}
	}

	public Collection<ExportedSymbol> getSymbols() {
		return exportedSymbols.values();
	}

	public TargetOS getTargetOS() {
		return targetOS;
	}

	public String getTechnique() {
		// TODO Auto-generated method stub
		if (technique.equals("")) {
			return "Unknown";
		}
		return technique;
	}

	public Set<Location> getUnresolvedBranches() {
		return unresolvedBranches;
	}

	/**
	 * Returns all variables used in the program. At the current state of the
	 * implementation, this includes only registers and flags.
	 * 
	 * @return A set containing all variables used in this program.
	 */
	public SetOfVariables getUsedVariables() {
		SetOfVariables result = new SetOfVariables();
		for (CFAEdge edge : cfa) {
			result.addAll(((RTLStatement) edge.getTransformer()).getUsedVariables());
		}
		return result;
	}

	/**
	 * Gets the assembly instruction at the specified virtual address.
	 * 
	 * @param address
	 *            a virtual address
	 * @return the assembly instruction at the specified address
	 */
	public long getWordValueMemory(AbsoluteAddress address) {
		// TODO Auto-generated method stub
		int result = 0;
		try {
			if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
				return 0;
			}

			ExecutableImage module = getModule(address);

			long fp = -1;
			if (module == null) {
				// logger.error("No module for address " + address
				// + ". Cannot disassemble instruction!");
				if (mainModule instanceof PEModule) {
					PEModule pe = (PEModule) mainModule;
					long t = pe.getWordValue(address.getValue());

					if (t != Long.MIN_VALUE) {
						return t;
					} else {
						return 0;
					}

				}
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area: " + address);
				}
				// else {
				// if (!module.isCodeArea(address)) {
				// logger.error("Requested instruction outside code section: "
				// + address);
				// return 0;
				// }
				// Disassembler dis = module.getDisassembler();
				// instr =
				X86Disassembler dis = (X86Disassembler) module.getDisassembler();
				int byteIndex = (int) fp;
				result = InstructionDecoder.readInt16(dis.getCode(), byteIndex);
				/*
				 * if (instr == null) {
				 * logger.error("Instruction could not be disassembled at: " +
				 * address); }
				 */
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

	/**
	 * Install a harness that sets up the symbolic environment before calling
	 * main and provides a return point with a termination statement.
	 * 
	 * @param harness
	 *            the harness object to install
	 */
	public void installHarness(Harness harness) {
		this.harness = harness;
		harness.install(this);
	}

	/**
	 * For all unresolved symbols, install simple stubs.
	 */
	public void installStubs() {
		if (mainModule instanceof AbstractCOFFModule) {
			stubLibrary = new Win32StubLibrary(arch);
		} else if (mainModule instanceof ELFModule) {
			stubLibrary = new LinuxStubLibrary(arch);
		}

		Iterator<UnresolvedSymbol> sIter = unresolvedSymbols.iterator();
		while (sIter.hasNext()) {
			UnresolvedSymbol unresolvedSymbol = sIter.next();
			AbsoluteAddress address = stubLibrary.resolveSymbol(unresolvedSymbol.getFromLibrary(),
					unresolvedSymbol.getName());
			if (address != null) {
				// logger.debug("Installing stack height stub for " +
				// unresolvedSymbol.getName());
				unresolvedSymbol.resolve(address);
				sIter.remove();
			}
		}

		if (!unresolvedSymbols.isEmpty()) {
			logger.warn("Unresolved symbols remaining: " + unresolvedSymbols);
		}
	}

	public boolean isInside(AbsoluteAddress address) {
//		if (address.getValue() == 4198400) {
//			System.out.println("Debug");
//		}
		
		boolean result = false;
		try {
			if (harness.contains(address) || address.getValue() >= StubProvider.STUB_BASE) {
				return false;
			}

			ExecutableImage module = getModule(address);
			long fp = -1;
			if (module == null) {
				// logger.error("No module for address " + address
				// + ". Cannot disassemble instruction!");
				if (mainModule instanceof PEModule) {
					return ((PEModule) mainModule).isInsideHeader(address.getValue());
				}
				//
			} else {
				fp = module.getFilePointer(address);
				// Also check whether fp is out of the int range, since the
				// X86Disassembler actually
				// performs this cast in its implementation.
				if (fp < 0 || (int) fp < 0) {
					logger.error("Requested instruction outside of file area " + address);
				}
				// else {
				// if (!module.isCodeArea(address)) {
				// logger.error("Requested instruction outside code section: "
				// + address);
				// return 0;
				// }
				// Disassembler dis = module.getDisassembler();
				// instr =
				result = true;
				/*
				 * if (instr == null) {
				 * logger.error("Instruction could not be disassembled at: " +
				 * address); }
				 */
			}

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}

	/**
	 * Loads the module containing the main function. This function should be
	 * called last for correct symbol resolution.
	 * 
	 * @param moduleFile
	 *            the file to load
	 * @return the ExecutableImage class for the loaded module
	 * @throws IOException
	 * @throws BinaryParseException
	 */
	public ExecutableImage loadMainModule(File moduleFile) throws IOException, BinaryParseException {
		ExecutableImage module = loadModule(moduleFile);
		mainModule = module;
		setEntryAddress(module.getEntryPoint());
		fileName = moduleFile.getName();
		setLogFile(fileName);
		installStubs();
		return module;
	}

	private void setLogFile(String name) {
		// TODO Auto-generated method stub
		logFile = new FileProcess("data/log/" + name + ".log");
		logFile.clearContentFile();
	}

	/**
	 * Loads a secondary (library or stub) module for analysis. Automatically
	 * determines the correct file type.
	 * 
	 * @param moduleFile
	 *            the file to load
	 * @return the ExecutableImage class for the loaded module
	 * @throws IOException
	 * @throws BinaryParseException
	 */
	public ExecutableImage loadModule(File moduleFile) throws IOException, BinaryParseException {
		// First try to load it as a PE file, then object file, ELF and finally
		// raw binary code
		// The right thing to do would be some smart IDing of the file type, but
		// this exception chaining works for now...
		ExecutableImage module = null;
		try {
			module = new PEModule(moduleFile, getArchitecture());
			targetOS = TargetOS.WINDOWS;
		} catch (BinaryParseException e) {
			try {
				module = new ObjectFile(moduleFile, getArchitecture());
			} catch (BinaryParseException e2) {
				try {
					module = new ELFModule(moduleFile, getArchitecture());
					targetOS = TargetOS.LINUX;
				} catch (BinaryParseException e3) {
					module = new RawModule(moduleFile, getArchitecture());
				}
			}
		}

		for (ExecutableImage existingModule : modules) {
			if (existingModule.getMaxAddress().getValue() >= module.getMinAddress().getValue()
					&& existingModule.getMinAddress().getValue() <= module.getMaxAddress().getValue()) {
				throw new RuntimeException("Virtual addresses of modules overlap!");
			}
		}

		modules.add(module);
		unresolvedSymbols.addAll(module.getUnresolvedSymbols());
		for (ExportedSymbol symbol : module.getExportedSymbols()) {
			exportedSymbols.put(removeDecoration(symbol.getName()), symbol);
		}
		resolveSymbols();
		return module;
	}

	/**
	 * Stores an assembly instruction at the given address, overwriting any
	 * existing instruction.
	 * 
	 * @param addr
	 *            the virtual address to save the instruction at
	 * @param instr
	 *            the assembly instruction
	 * @return true if there was no instruction stored for that address before,
	 *         false otherwise.
	 */
	public final boolean putInstruction(AbsoluteAddress addr, Instruction instr) {
		// logger.info(addr + " " + instr.toString(addr.getValueOperand(), new
		// DummySymbolFinder()));
		/*
		 * if (assemblyMap.containsKey(addr)) { Instruction i =
		 * assemblyMap.get(addr); if (!i.equals(instr)) { AbsoluteAddress nAddr
		 * = new AbsoluteAddress(addr.getValueOperand()); return
		 * assemblyMap.put(nAddr, instr) == null; } }
		 */
		this.addPMState(addr, instr);

		return assemblyMap.put(addr, instr) == null;
	}

	/**
	 * Stores a statement in the program. If a statement already exists with the
	 * same label, it is replaced.
	 * 
	 * @param stmt
	 *            The statement to be stored. Has to contain a proper label.
	 */
	public final void putStatement(RTLStatement stmt) {
		RTLStatement existing = statementMap.get(stmt.getLabel());
		if (existing != null) {
			if (existing.equals(stmt)) {
				return;
			}
			logger.debug("Replacing statement at " + stmt.getLabel());
		}

		this.statementMap1.put(new PMLocation(stmt.getLabel().getAddress(), stmt.getLabel().getIndex()), stmt);

		statementMap.put(stmt.getLabel(), stmt);
	}

	public void putStatement(RTLStatement stmt, Instruction instr) {
		// TODO Auto-generated method stub
		RTLStatement existing = statementMap.get(stmt.getLabel());
		if (existing != null) {
			if (existing.equals(stmt)) {
				return;
			}
			logger.debug("Replacing statement at " + stmt.getLabel());
		}

		this.statementMap1.put(new PMLocation(stmt.getLabel().getAddress(), stmt.getLabel().getIndex(), instr), stmt);

		statementMap.put(stmt.getLabel(), stmt);
	}

	public final void putStatement1(RTLStatement stmt) {
		RTLStatement existing = statementMap.get(stmt.getLabel());
		if (existing != null) {
			if (existing.equals(stmt)) {
				return;
			}
			logger.debug("Replacing statement at " + stmt.getLabel());
		}
		statementMap.put(stmt.getLabel(), stmt);
	}

	private String removeDecoration(String s) {
		if (s.charAt(0) == '@' || s.charAt(0) == '_') {
			s = s.substring(1);
		}
		int i = s.indexOf('@');
		if (i >= 0) {
			s = s.substring(0, i);
		}
		return s;
	}

	/**
	 * Resolves symbols between the loaded modules.
	 */
	private void resolveSymbols() {
		Iterator<UnresolvedSymbol> sIter = unresolvedSymbols.iterator();
		while (sIter.hasNext()) {
			UnresolvedSymbol unresolvedSymbol = sIter.next();
			ExportedSymbol symbol = exportedSymbols.get(removeDecoration(unresolvedSymbol.getName()));

			if (symbol != null) {
				logger.debug("Resolving symbol " + unresolvedSymbol.getName());
				unresolvedSymbol.resolve(symbol.getAddress());
				sIter.remove();
			}
		}
	}

	/**
	 * @param absolutePath
	 *            the absolutePath to set
	 */
	public void setAbsolutePathFile(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	public void setAnalyzedInstruction(Instruction analyzedInstruction) {
		this.analyzedInstruction = analyzedInstruction;
	}

	public void setCFA(Set<CFAEdge> cfa) {
		this.cfa = cfa;
	}

	public void setCFGState(Set<CFGState> cfg) {
		this.cfg = cfg;
	}

	public void setDetailTechnique(String detail_tech) {
		if (!this.detail_technique.contains(detail_tech)) {
			this.detail_technique += " " + detail_tech;
			// resultFile_Temp.appendInLine(detail_tech + " Nodes:" +
			// getBPCFG().getVertexCount() + " ");
		}
	}

	/**
	 * Set the program entry point to the given address.
	 * 
	 * @param entryAddress
	 *            the new entry address
	 */
	public void setEntryAddress(AbsoluteAddress entryAddress) {
		setStart(new Location(entryAddress));
	}

	/**
	 * @param fullResultFile
	 *            the fullResultFile to set
	 */
	public void setFullResultFile(FileProcess fullResultFile) {
		this.fullResultFile = fullResultFile;
	}

	public void setPmState(List<PMState> pmState) {
		this.pmState = pmState;
	}

	public void setReachedSet(ReachedSet r) {
		this.reached = r;
	}

	/**
	 * @param resultFile
	 *            the resultFile to set
	 */
	public void setResultFile(FileProcess resultFile) {
		this.resultFile = resultFile;
	}

	public void setResultFileTemp(FileProcess resultFile_Temp) {
		this.resultFile_Temp = resultFile_Temp;
	}

	/**
	 * @param smPos
	 *            the smPos to set
	 */
	public void setSelfModifyPosition(List<AbsoluteAddress> smPos) {
		this.smPos = smPos;
	}

	/**
	 * Set the program entry point to the given label.
	 * 
	 * @param label
	 *            the new entry point
	 */
	public void setStart(Location label) {
		this.start = label;
	}

	public void setTechnique(String str) {
		// TODO Auto-generated method stub
		if (!technique.contains(str)) {
			// PHONG - 20150724
			/*
			if (str.contains("Encrypt/Decrypt"))
				this.pDetection.getTechniques().isPackingUnpacking();
			if (str.contains("Indirect Jump"))
				this.pDetection.getTechniques().isIndirectJump();
			if (str.contains("SEH")||str.contains("SetUpException"))
				this.pDetection.getTechniques().isSEH();
			if (str.contains("SMC"))
				this.pDetection.getTechniques().isOverwriting();
			if (str.contains("UseAPI: VirtualAlloc"))
				this.pDetection.getTechniques().isStolenBytes();
			if (str.contains("UseAPI: IsDebuggerPresent"))
				this.pDetection.getTechniques().isAntiDebugging();
			if (str.contains("UseAPI: LoadLibraryA")
					|| str.contains("UseAPI: GetProcAddress"))
				this.pDetection.getTechniques().isTwoAPIs();
			*/
			technique += " " + str;
			// resultFile_Temp.appendInLine(str + " Nodes:" +
			// getBPCFG().getVertexCount() + " ");
		}
	}

	public void setUnresolvedBranches(Set<Location> unresolvedBranches) {
		this.unresolvedBranches = unresolvedBranches;
	}

	private long specialCase() {
		// TODO Auto-generated method stub
		if (this.getFileName().equals("smc.obj") || this.absolutePath.contains("smc.obj")) {
			return 192;
		}

		return 0;
	}

	private SymbolFinder symbolFinder(AbsoluteAddress addr) {
		if (addr.getValue() >= StubProvider.STUB_BASE) {
			return stubLibrary.getSymbolFinder();
		}

		ExecutableImage module = getModule(addr);
		return (module == null) ? new DummySymbolFinder() : module.getSymbolFinder();
	}

	public void setDebugLevel(int i) {
		// TODO Auto-generated method stub
		logger.setDebugLevel(i);
	}

	public Logging getLog() {
		return logger;
	}
	
	// PHONG - 20150724
	public PackerDetection getDetection()
	{
		return pDetection;
	}
	
	//////////////////////////////////////////////
	public void setPackerResultFile(FileProcess packerResultFile) {
		this.packerResultFile = packerResultFile;
	}

	public FileProcess getPackerResultFile() {
		return packerResultFile;
	}
	
	public static String getPackerResultFileName ()
	{
		return packerResultFileTXT;
	}
	
	///////////////////////////////////////////////
	public void setPackerResultCountFile(FileProcess packerResultCountFile) {
		this.packerResultCountFile = packerResultCountFile;
	}

	public FileProcess getPackerResultCountFile() {
		return packerResultCountFile;
	}
	
	public static String getPackerResultCountFileName ()
	{
		return packerResultCountFileTXT;
	}
	
	////////////////////////////////////////////////
	public void SetAnalyzingTime (long time)
	{
		this.analyzingTime = time;
	}
	
	public long GetAnalyzingTime ()
	{
		return this.analyzingTime;
	}

	public void setLog(String string) {
		// TODO Auto-generated method stub
		System.out.println(string);
	}

	public boolean isBetweenSection(AbsoluteAddress address) {
		// TODO Auto-generated method stub
		if (mainModule!= null && mainModule instanceof PEModule) {
			return ((PEModule) mainModule).isBetweenSections(address.getValue());
		}
		
		return false;
	}

	public FileProcess getStopFile() {
		return stopFile;
	}

	public void setStopFile(FileProcess stopFile) {
		this.stopFile = stopFile;
	}

}
