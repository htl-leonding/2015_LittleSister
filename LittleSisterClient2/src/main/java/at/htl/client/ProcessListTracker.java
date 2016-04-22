
package at.htl.client;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class ProcessListTracker {
    private static List<String> processes = new LinkedList<>();
    
    public static enum Processes implements IProcessListingStrategy {
        ALL_PROCESSES;
 
        private IProcessListingStrategy processListing = selectProcessListingStrategy();
 
        @Override
        public void listProcesses() throws Exception {
            processListing.listProcesses();
        }
 
        private IProcessListingStrategy selectProcessListingStrategy() {
            //todo add support for mac ...
            return isWindows() ? new WinProcessListingStrategy() : new LinuxProcessListingStrategy();
        }
 
        private static boolean isWindows() {
            return System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
 
        }
    }
 
    static interface IProcessListingStrategy {
        void listProcesses() throws Exception;
    }
 
    static abstract class AbstractNativeProcessListingStrategy implements IProcessListingStrategy {
        @Override
        public void listProcesses() throws Exception {
            Process process = makeProcessListingProcessBuilder().start();
            Scanner scanner = new Scanner(process.getInputStream());
            while (scanner.hasNextLine()) {
                processes.add(scanner.nextLine().split(" ")[0]);
            }
            scanner.close();
            process.waitFor();
        }
 
        protected abstract ProcessBuilder makeProcessListingProcessBuilder();
    }
 
    static class WinProcessListingStrategy extends AbstractNativeProcessListingStrategy {
        @Override
        protected ProcessBuilder makeProcessListingProcessBuilder() {
            return new ProcessBuilder("cmd", "/c", "tasklist");
        }
    }
 
    static class LinuxProcessListingStrategy extends AbstractNativeProcessListingStrategy {
        @Override
        protected ProcessBuilder makeProcessListingProcessBuilder() {
            return new ProcessBuilder("ps", "-e");
        }
    }
}