import com.amazonaws.services.s3.transfer.*;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("This jar will download from s3");
        if (args.length != 2) {
            System.out.println("args size must be 2 current size is " + args.length);
            System.exit(-1);
        }

        System.out.println("keyName = " + args[0]);
        System.out.println("filePath = " + args[1]);
        Scanner scanner = new Scanner(System.in);
        System.out.println("press Y to continue");
        String s = scanner.nextLine();
        if (s.contains("Y") || s.contains("y")) {
            String existingBucketName = "MyBucketName";
            String keyName = args[0];
            String filePath = args[1];
            TransferManagerBuilder builder = TransferManagerBuilder.standard();
            TransferManager tm = builder.build();
            System.out.println("TransferManager created");
            // TransferManager processes all transfers asynchronously, so this call will return immediately.
            Upload upload = tm.upload(existingBucketName, keyName, new File(filePath));
            showTransferProgress(upload);
            System.out.println("Shutting down the Transfer Manager");
            tm.shutdownNow();
            System.out.println("Transfer Manager shutdown \n terminating");
        }
        scanner.close();
    }

    private static void printProgressBar(double pct) {
        // if bar_size changes, then change erase_bar (in eraseProgressBar) to match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int) (bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
                empty_bar.substring(0, bar_size - amt_full));
        System.out.println(" " + pct + "%");
    }

    private static void eraseProgressBar() {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }

    private static void showTransferProgress(Transfer xfer) {
        // print the transfer's human-readable description
        System.out.println(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(15000); //every 15 second
            } catch (InterruptedException e) {
                return;
            }
            TransferProgress progress = xfer.getProgress();
            double pct = progress.getPercentTransferred();
            eraseProgressBar();
            printProgressBar(pct);
        } while (!xfer.isDone());
        // print the final state of the transfer.
        Transfer.TransferState xfer_state = xfer.getState();
        System.out.println("upload state -> : " + xfer_state);
    }
}
