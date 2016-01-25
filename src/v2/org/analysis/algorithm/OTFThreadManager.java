/**
 * Project: BE-PUMv2
 * Package name: v2.org.analysis.algorithm
 * File name: OTFThreadManager.java
 * Created date: Dec 3, 2015
 * Description:
 */
package v2.org.analysis.algorithm;

import java.util.List;

import v2.org.analysis.algorithm.OTFModelGeneration.OTFThread;
import v2.org.analysis.log.BPLogger;
//import v2.org.analysis.algorithm.OTFModelGeneration.OTFThread;
import v2.org.analysis.path.BPPath;

/**
 * @author Yen Nguyen
 *
 */
public class OTFThreadManager {

	private static final boolean IS_MULTI_THREAD = false;
	private static final int DEFAULT_NUM_OF_CORES = 1;

	/**
	 * Singleton instance of {@link OTFThreadManager} class
	 */
	private static volatile OTFThreadManager mInstance = null;
	private static int mNumberOfCore = 0;

	/**
	 * The constructor of {@link OTFThreadManager} class. This method just be
	 * called if only the caller is getIntance.
	 * 
	 * @throws Exception
	 *             will be thrown if the caller is not getInstance
	 * 
	 */
	public OTFThreadManager() throws Exception {
		StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

		boolean isRightAccess = false;
		if (stackTraceElements.length >= 3) {
			String className = OTFThreadManager.class.getName();
			if (stackTraceElements[1].getClassName().equals(className)
					&& stackTraceElements[1].getMethodName().equals("<init>")
					&& stackTraceElements[2].getClassName().equals(className)
					&& stackTraceElements[2].getMethodName().equals("getInstance")) {
				isRightAccess = true;
			}
		}

		/**
		 * Check if user violates singleton design pattern
		 */
		if (!isRightAccess) {
			throw new Exception(
					"Violate Singleton Design Pattern! Please call getInstance static method of this class to get the instance!");
		}

		if (IS_MULTI_THREAD) {
			OTFThreadManager.mNumberOfCore = Runtime.getRuntime().availableProcessors();
		} else {
			OTFThreadManager.mNumberOfCore = DEFAULT_NUM_OF_CORES;
		}
	}

	/**
	 * Get singleton instance of this class
	 * 
	 * @return {@link OTFThreadManager} instance
	 */
	public static synchronized OTFThreadManager getInstance() {
		if (mInstance == null) {
			try {
				mInstance = new OTFThreadManager();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mInstance;
	}

	private int mNumberOfCurrentThreads = 0;

	/**
	 * Inform the manager that you want to start a new OTFThread. This method
	 * will return a number of current threads (after having started) if this
	 * number is smaller than a number of cores of processor; otherwise it will
	 * throw an exception.
	 * 
	 * @return Number of current threads after having started
	 * 
	 * @throws Exception
	 *             If number of current thread is larger than number of cores.
	 */
	private synchronized int startNewThread() throws Exception {
		synchronized (this) {
			if (this.isCanStart()) {
				this.mNumberOfCurrentThreads++;
			} else {
				throw new Exception("Reached to a maximum of cores!");
			}
		}
		return this.mNumberOfCurrentThreads;
	}

	/**
	 * Inform the manager that you want to finish an OTFThread. This method will
	 * return a number of current threads (after having finished). If this
	 * number is equal to zero, it will let the main thread (running algorithm)
	 * resume.
	 * 
	 * @return Number of current threads after having finished
	 * 
	 * @throws Exception
	 *             If number of current thread is smaller than zero.
	 */
	private synchronized int finishThread() throws Exception {
		synchronized (this) {
			if (this.mNumberOfCurrentThreads > 0) {
				this.mNumberOfCurrentThreads--;

				// Notify waiting main thread to resume
				if (this.mNumberOfCurrentThreads == 0) {
					synchronized (this) {
						this.notifyAll();
						BPLogger.debugLogger.info("************************ END ***************************");
					}
				}
			} else {
				throw new Exception("There is NO thread to finish!");
			}
		}
		return this.mNumberOfCurrentThreads;
	}

	public synchronized boolean isCanStart() {
		// System.out.println(String.format("%d-%d",
		// this.mNumberOfCurrentThreads, this.mNumberOfCore));
		if (this.mNumberOfCurrentThreads < OTFThreadManager.mNumberOfCore) {
			return true;
		} else {
			return false;
		}
	}

	public synchronized void check(OTFModelGeneration otfModelGeneration, List<BPPath> pathList) throws Exception {
		if (otfModelGeneration == null || pathList == null) {
			BPLogger.debugLogger.error(String.format("[OTFThreadManager] NULL INPUT - otf:%s,pathList:%s",
					otfModelGeneration, pathList));
		}
		if (pathList != null && pathList.size() > 0) {
			BPLogger.debugLogger.info(String.format("[OTFThreadManager] check pathListSize:%d", pathList.size()));
		}
		if (this.isCanStart() && otfModelGeneration != null && pathList != null && pathList.size() > 0) {
			BPPath path = pathList.remove(pathList.size() - 1);

			OTFThread thread = otfModelGeneration.new OTFThread(path);
			thread.start();
			BPLogger.debugLogger.info(String.format("[OTFThreadManager] START location:%s,numOfCurrentThreads:%d/%d",
					path.getCurrentState().getLocation().toString(), this.mNumberOfCurrentThreads, OTFThreadManager.mNumberOfCore));
		}
	}

	/**
	 * 
	 * @author Yen Nguyen
	 *
	 */
	static public abstract class OTFThreadBase extends Thread {
		public abstract void execute();

		private void beforeExecute() {
			try {
				OTFThreadManager.getInstance().startNewThread();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("_____________________________________________________________________________");
			System.out.println("_________________________START NEW THREAD OTF________________________________");
			System.out.println("_____________________________________________________________________________");
		}

		private void afterExecute() {
			try {
				OTFThreadManager.getInstance().finishThread();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void afterLoop(OTFModelGeneration otfModelGeneration, List<BPPath> pathList) {
			try {
				OTFThreadManager.getInstance().check(otfModelGeneration, pathList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			this.beforeExecute();
			this.execute();
			this.afterExecute();
		}
	}

}