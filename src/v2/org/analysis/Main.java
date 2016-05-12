/*
 * Main.java - This file is part of the Jakstab project.
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

package v2.org.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.UIManager;

import org.jakstab.Algorithm;
import org.jakstab.Options;
import org.jakstab.Program;
import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.loader.BinaryParseException;
import org.jakstab.loader.DefaultHarness;
import org.jakstab.loader.HeuristicHarness;
import org.jakstab.ssl.Architecture;
import org.jakstab.util.Characters;
import org.jakstab.util.Logger;

import v2.org.analysis.algorithm.OTFModelGeneration;
import v2.org.analysis.algorithm.OTFThreadManager;
import v2.org.analysis.cfg.BPCFG;
import v2.org.analysis.packer.PackerManager;
import v2.org.analysis.statistics.FileProcess;
import antlr.ANTLRException;

public class Main {
	static {
		// Yen Nguyen: With so many System.out.print... calls,
		// the console will not able to show all of informations you want.
		// Set isLog true and them will be saved into Log.log file for you.
		// boolean isLog = true;
		boolean isLog = false;
		if (isLog) {
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
			Date date = new Date();
			String logFile = "Log-" + dateFormat.format(date) + ".log";
			setLogToFile(logFile);
		}
	}

	private static void setLogToFile(String logFile) {
		try {
			System.out.println("================== LOG TO FILE ==================");
			PrintStream out = new PrintStream(new FileOutputStream(logFile));
			System.setOut(out);
			logger = Logger.getLogger(Main.class);
			System.out.println("================== DEBUG ==================");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Logger logger = Logger.getLogger(Main.class);
	private final static String version = "2.0";
	private static volatile Algorithm activeAlgorithm;
	private static volatile Thread mainThread;
	public static boolean isGui = false;

	public static String[] SplitUsingTokenizer(String Subject, String Delimiters) {
		StringTokenizer StrTkn = new StringTokenizer(Subject, Delimiters);
		ArrayList<String> ArrLis = new ArrayList<String>(Subject.length());
		while (StrTkn.hasMoreTokens()) {
			ArrLis.add(StrTkn.nextToken());
		}
		return ArrLis.toArray(new String[0]);
	}

	/*
	 * public static void main(String[] args) { //Main m = new Main(); String
	 * fileName = "asm/sefm14/pony1.exe"; Main.process("-m " + fileName); }
	 */

	public static void main(String[] args) {
		mainThread = Thread.currentThread();

		// StatsTracker stats = StatsTracker.getInstance();

		// Parse command line
		// System.out.print("Enter file name:");
		System.out.print("Enter file name:");
		String in = "";
		String pathVirus = "";
		// Path Virus
		pathVirus = "asm/packer/";
		// pathVirus = "asm/packer/";
		// in = "api_testv2.exe"; // 2064 2151 26s ?
		in = "api_test.exe"; // 158 160 0.1s x
		in = "api_test_upx_N.exe"; // 323 353 6s x
//		in = "api_test_fsg.exe"; // 244 268 3s x
//		in = "demo1_fsg.exe"; // 244 268 3s x
//		in = "api_testv2_fsg.exe"; // 244 268 3s x
//		in = "api_test_pecompact.exe"; // 1127 1178 28s x
//		in = "api_testv2_pecompact.exe";
//		in = "bof_aspack.exe";
//		in = "hostname_upx.exe";
//		in = "api_test_npack.exe"; // 602 639 10s x
		// in = "api_test_yoda.1.2.exe"; // 625 659 173s x
//		in = "api_test_yoda.1.3.exe"; // 924 960 163s x
		// in = "api_test_petite_2.3.exe"; // 1569 1637 86s x
//		in = "api_test_aspack.exe"; // 1047 1112 73s x
		// in = "api_test_mpress.exe"; // 459 489 103 x
		// in = "api_test_wwpack32.exe"; // 329 360 4s x
		// in = "api_test_exepack.exe"; // 323 353 6s x
		// in = "api_test_WinUpack.exe"; // 443 490 19s
		// in = "api_test_telock.exe"; // x
		// in = "api_test_RLPack.exe"; // 467 501 212s x
//		in = "api_test_upack.exe"; // 443 490 21s x
		// in = "api_test_mew.exe"; // 265 291 5s
		// in = "api_test_BJFNT.exe"; // x
		// in = "api_test_ExeStealth.exe"; // 735 770 220s x
		// in = "api_test_eXPressor.exe"; // 1172 1233 85s x
		// in = "api_test_NoodleCrypt.exe"; // 706 757 34s x

		// Undone
		// in = "api_test_EXEfog.exe";
		// in = "api_test_EXEJonier.exe";
		// in = "api_test_KKrunchy.exe"; // Disassembled at: 0x003f11ab
		// in = "api_test_Enigma.exe"; // Run long time
		// in = "api_test_ALEXProtector.exe"; // Error VirtualAlloc
		// in = "api_test_ACProtect.exe"; // Error Call Memory Run OllyDbg later
		// in = "protected_CRYPToCRACk.exe"; // Error file
		// in = "api_test_diet.exe"; // Error parse file
		// in = "api_test_PolyCryptPE.exe";
		// in = "api_test_Yodaprotect.exe";
		// in = "api_test_zprotect.exe"; // Run long time
		// in = "api_test_sercure.exe"; // 5328 5625 270s
		// in = "api_test_MSLRH.exe"; // Error with Int3
		// in = "UnPackMe_MSLRH0.32a.exe"; // RDTSC problem
		// in = "api_test_pespin.exe";
		// in = "demo1_fastpack.exe"; // 47 49 98s
		// in = "api_test_armadillo.exe";
		// in = "api_test_asprotect.exe";
		// in = "api_test_thermida.exe";
		// in = "api_test_vmprotect.exe"; // 488 532 456s
		// in = "api_test_pelock.exe"; // Run too long
		// in = "api_test_asprotect.exe";
		// in = "api_test_pebundle.exe";
		// in = "api_test_polycryptpe_1.exe"; // 106 109
		// in = "api_test_LZEXE.EXE"; // Error parse file
		// in = "api_test_PE_Ninja.exe";
		// in = "api_test_Morphine_3.5.exe";

		// in = "demo1.exe"; // 13405 13404 14s x
		// in = "demo1_upx.exe"; // 13563 13583 465 x
		// in = "demo1_fsg.exe"; // 13493 13510 13s 0111001010111110-NONE
		// in = "demo1_pecompact.exe"; // 788 828 225s
		// in = "demo1_npack.exe"; // 13851 13881 685s x
		// in = "demo1_yoda.exe";
		// in = "demo1_petite.exe"; // Error
		// in = "demo1_aspack.exe"; // 14278 14334 907s x
		// in = "demo1_fastpack.exe";

		// pathVirus = "asm/virus/";
		// pathVirus = "C:/Work/Virus/viruses-20070914/vx.netlux.org/";
		// in = "Email-Worm.Win32.Mydoom.az"; // 641 651
		// in =
		// "003ba46362d1c2643a690cd7e912441b0ee04ee0f8026789f677b873389c0361";

//		pathVirus = "asm/packer/";
//		pathVirus = "asm/vx.netlux.org/";
//		pathVirus = "asm/testcase/";
//		pathVirus = "asm/virus/";
//		in = "app5win.exe";
//		in = "peb.exe";
//		in = "hostname.exe";
//		// in = "Net-Worm.Win32.Sasser.a";
//		in = "multiThread1.exe";
//		in = "multiThread2.exe";
//		in = "multiThread3_FF.exe";
//		// in = "multiThread3_FFF.exe";
//		// in = "multiThread3_FFFF.exe";
//		in = "Rdws.exe_dc3c90084e8c.bin-unpacked.bin";
				
		String path = pathVirus + in;
		isGui = false;
		// YenNguyen: For jar file export
		if (!Main.class.getResource("Main.class").toString().startsWith("file")) {
			if (args.length > 0) {
				for (String input : args) {
					if (input.charAt(0) == '-') {
						input = input.toLowerCase();
						if (input.equals("-gui")) {
							try {
								isGui = true;
								// Set System L&F
								UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
							} catch (Exception e) {
								// handle exception
							}
							new MainWindows();
						} else if (input.equals("-log")) {
							String fName = "";
							for (String arg : args) {
								if (arg.contains("\\")) {
									fName = arg.substring(arg.lastIndexOf("\\") + 1, arg.length());
								} else {
									fName = arg;
								}
							}
							String logFile = "Log - " + fName + ".log";
							setLogToFile(logFile);
						} else if (input.contains("-buffer")) {
							String size = input.substring(7);
							try {
								int buffer = Integer.valueOf(size);
								OTFThreadManager.getInstance().setSizeThreadBuffer(buffer);
							} catch (Exception e) {
								System.out.println(e.toString());
							}
						} else if (input.contains("-multithread")) {
							OTFThreadManager.getInstance().setMultiThread(true);							
						} else if (input.contains("-detectpacker")) {
							PackerManager.getInstance().setDetectPacker(true);							
						} else if (input.contains("-detectheader")) {
							PackerManager.getInstance().setDetectHeader(true);							
						}
					} else {
						path = input;
					}
				}
			} else {
				@SuppressWarnings("resource")
				Scanner user_input = new Scanner(System.in);
				path = user_input.next();
			}
		}

		if (!isGui) {
			System.out.println(path);
			analyzeFile(path);
		}
	}

	private static void runAlgorithm(Algorithm a) {
		activeAlgorithm = a;
		a.run();
		activeAlgorithm = null;
	}

	private static String getBaseFileName(File file) {
		String baseFileName = file.getAbsolutePath();
		// Get name of the analyzed file without file extension if it has one
		if (file.getName().contains(".")) {
			int dotIndex = file.getPath().lastIndexOf('.');
			if (dotIndex > 0) {
				// baseFileName = file.getPath().substring(0, dotIndex);
			}
		}
		return baseFileName;
	}

	public static void analyzeFile(String file) {
		// TODO Auto-generated method stub
		mainThread = Thread.currentThread();

		String[] arg = SplitUsingTokenizer("-m " + file, " ");
		// String[] arg = SplitUsingTokenizer(pathFile, " ");
		// String[] arg = SplitUsingTokenizer("-s -m asm/chosenFile/processing/"
		// + in, " ");
		Options.parseOptions(arg);
		// Options.parseOptions(in.next().split(" "));

		logger.info(Characters.DOUBLE_LINE_FULL_WIDTH);
		logger.info("                         Jakstab " + version);
		logger.info(Characters.DOUBLE_LINE_FULL_WIDTH);

		logger.error("Start analyzing file with BE-PUM");

		// ///////////////////////
		// Parse SSL file

		Architecture arch;
		try {
			arch = new Architecture(Options.sslFilename.getValue());
			// arch = new Architecture(fName.next());
		} catch (IOException e) {
			logger.fatal("Unable to open SSL file!", e);
			return;
		} catch (ANTLRException e) {
			logger.fatal("Error parsing SSL file!", e);
			return;
		}

		// ///////////////////////
		// Parse executable

		Program program = Program.createProgram(arch);
		program.setDebugLevel(3);

		File mainFile = new File(Options.mainFilename).getAbsoluteFile();

		String baseFileName = null;
		long overallStartTime = System.currentTimeMillis();
		try {
			// Load additional modules
			for (String moduleName : Options.moduleFilenames) {
				logger.fatal("Parsing " + moduleName + "...");
				File moduleFile = new File(moduleName).getAbsoluteFile();
				program.loadModule(moduleFile);

				// If we are processing drivers, use the driver's name as base
				// name
				if (Options.wdm.getValue() && moduleFile.getName().toLowerCase().endsWith(".sys")) {
					baseFileName = getBaseFileName(moduleFile);
				}
			}
			// Load main module last
			logger.error("Parsing main module " + Options.mainFilename + "...");
			program.loadMainModule(mainFile);

			// Use main module as base name if we have none yet
			// reserved for drivers, ignore
			if (baseFileName == null) {
				baseFileName = getBaseFileName(mainFile);
			}
			program.setAbsolutePathFile(baseFileName);
		} catch (FileNotFoundException e) {
			logger.fatal("File not found: " + e.getMessage());
			return;
		} catch (IOException e) {
			logger.fatal("IOException while parsing executable!", e);
			// e.printStackTrace();
			return;
		} catch (BinaryParseException e) {
			logger.fatal("Error during parsing!", e);
			// e.printStackTrace();
			return;
		}
		logger.info("Finished parsing executable.");

		// Change entry point if requested
		if (Options.startAddress.getValue() > 0) {
			logger.verbose("Setting start address to 0x" + Long.toHexString(Options.startAddress.getValue()));
			program.setEntryAddress(new AbsoluteAddress(Options.startAddress.getValue()));
		}

		// Add surrounding "%DF := 1; call entrypoint; halt;"
		program.installHarness(Options.heuristicEntryPoints.getValue() ? new HeuristicHarness() : new DefaultHarness());

		// stats.record(baseFileName.substring(slashIdx));
		// stats.record(version);

		// StatsPlotter.create(baseFileName + "_states.dat");
		// StatsPlotter.plot("#Time(ms)\tStates\tInstructions\tGC Time\tSpeed(st/s)");

		// Catches control-c and System.exit
		Thread shutdownThread = new Thread() {
			@Override
			public void run() {
				if (mainThread.isAlive() && activeAlgorithm != null) {
					// stop = true; // Used for CFI checks
					activeAlgorithm.stop();
					try {
						mainThread.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(shutdownThread);

		// Add shutdown on return pressed for eclipse
		if (!Options.background.getValue() && System.console() == null) {
			logger.info("No console detected (eclipse?). Press return to terminate analysis and print statistics.");
			Thread eclipseShutdownThread = new Thread() {
				@Override
				public void run() {
					try {
						System.in.read();
					} catch (IOException e) {
						e.printStackTrace();
					}
					System.exit(1);
				}
			};
			// yices.dll blocks for input on load for some reason, so load it
			// before we start reading from System.in
			// If you are having problems with that, uncomment the next line
			// org.jakstab.solver.yices.YicesWrapper.getVersion();
			eclipseShutdownThread.start();
		}

		// Necessary to stop shutdown thread on exceptions being thrown
		try {

			// ///////////////////////
			// On-the-fly Model Generation
			OTFModelGeneration otfMG = new OTFModelGeneration(program);
			// long customTime = System.currentTimeMillis();
			// Execute the algorithm
			try {
				runAlgorithm(otfMG);
			} catch (RuntimeException r) {
				logger.error("!! Runtime exception during Control Flow Reconstruction! Trying to shut down gracefully.");
				r.printStackTrace();
			}
			// long overallEndTime = System.currentTimeMillis();
			// ProgramGraphWriter graphWriter = new ProgramGraphWriter(program);

			if (!otfMG.isCompleted()) {
				System.out.println(Characters.starredBox("WARNING: Analysis interrupted, CFG might be incomplete!"));
			} else {
				System.out.println(Characters.starredBox("Analysis finished, CFG is complete!"));
			}

			if (!otfMG.isSound()) {
				logger.error(Characters.starredBox("WARNING: Analysis was unsound!"));
			}
			program.generageCFG("/asm/cfg/" + program.getFileName());
			BPCFG cfg = program.getBPCFG();

			long overallEndTime = System.currentTimeMillis();
			System.out.println(Characters.DOUBLE_LINE_FULL_WIDTH);
			System.out.println("   Statistics for On-The-Fly Model Generation of BE-PUM");
			System.out.println(Characters.DOUBLE_LINE_FULL_WIDTH);
			System.out.println("   Filename:                     " + program.getFileName());
			System.out.println("   Runtime:                     "
					+ String.format("%8dms", (overallEndTime - overallStartTime)));
			System.out.println("   Instructions:                        "
					+ String.format("%8d", cfg.getInstructionCount()));
			System.out.println("   Nodes:                        " + String.format("%8d", cfg.getVertexCount()));
			System.out.println("   Edges:                        " + String.format("%8d", cfg.getEdgeCount()));
			// FileProcess fullResultFile = program.getFullResultFile();

			// Kernel32DLL.INSTANCE.SetCurrentDirectory(new
			// WString(System.getProperty("user.dir")));
			// fullResultFile.appendFile(Characters.DOUBLE_LINE_FULL_WIDTH);
			// fullResultFile.appendFile("   Statistics for On-The-Fly Model Generation of BE-PUM");
			// fullResultFile.appendFile(Characters.DOUBLE_LINE_FULL_WIDTH);
			// fullResultFile.appendFile("   Filename:                     " +
			// program.getFileName());
			// fullResultFile.appendFile("   Runtime:                     "
			// + String.format("%8dms", (overallEndTime - overallStartTime)));
			// fullResultFile.appendFile("   Instructions:                        "
			// + String.format("%8d", cfg.getInstructionCount()));
			// fullResultFile.appendFile("   Nodes:                        " +
			// String.format("%8d", cfg.getVertexCount()));
			// fullResultFile.appendFile("   Edges:                        " +
			// String.format("%8d", cfg.getEdgeCount()));

			FileProcess resultFile = program.getResultFile();
			resultFile.appendFile(program.getFileName() + "\t"
					+ String.format("%8dms", (overallEndTime - overallStartTime)) + "\t"
					+ String.format("%8d", cfg.getVertexCount()) + "\t" + String.format("%8d", cfg.getEdgeCount())
					+ "\t" + String.format("%b", OTFThreadManager.getInstance().isMultiThread()) + "\t"
					+ String.format("%8d", OTFThreadManager.getInstance().getSizeThreadBuffer()));
			//
			// program.getResultFileTemp().appendInLine(program.getDetailTechnique());
			// program.getResultFileTemp().appendInLine(
			// '\t' + String.format("%8dms", (overallEndTime -
			// overallStartTime)) + "\t"
			// + String.format("%8d", cfg.getVertexCount()) + "\t"
			// + String.format("%8d", cfg.getEdgeCount()));

			// Comparison between IDA and BE-PUM
			// String f = program.generatePathFileName(baseFileName);
			// new DOTComparison().exportComparison(baseFileName);

			// PHONG: 20150508
			// DETECT VIA OTF IF COMPLETED
			// ////////////////////////////////////////////////////////////////////
			// program.SetAnalyzingTime(overallEndTime - overallStartTime);
			// program.getDetection().updateBackupDetectionState(program,
			// otfMG);
			// program.getDetection().setToLog(program);
			if (PackerManager.getInstance().isDetected()) {
				PackerManager.getInstance().outputToFile(program.getFileName());
			}

			try {
				Runtime.getRuntime().removeShutdownHook(shutdownThread);
				// YenNguyen: Start GUI from this class
				if (!isGui) {
					System.exit(0);
				}
			} catch (IllegalStateException e) {
				// Happens when shutdown has already been initiated by Ctrl-C or
				// Return
				// e.printStackTrace();
			} finally {
			}
		} catch (Throwable e) {
			System.out.flush();
			e.printStackTrace();
			Runtime.getRuntime().removeShutdownHook(shutdownThread);
			// Kills eclipse shutdown thread
			System.exit(1);
		}
	}

}
