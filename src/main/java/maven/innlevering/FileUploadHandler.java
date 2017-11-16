package maven.innlevering;

import maven.innlevering.database.DBValidationHandler;
import maven.innlevering.database.DBUploadAsThread;
import maven.innlevering.exception.CustomFileNotFoundException;
import maven.innlevering.exception.CustomIOException;
import maven.innlevering.exception.CustomSQLException;

import java.io.File;
import java.util.ArrayList;

/**
 * Class is used to start thread jobs. It goes through the list of files and asign a file to each thread.
 * Created by hakonschutt on 26/09/2017.
 */
public class FileUploadHandler {

    /**
     * Method initiate the thread job
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public void startInputScan() throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        String[] files = getAllFiles();
        Thread[] threads = new Thread[files.length];

        for (int i = 0; i < files.length; i++){
            DBUploadAsThread job = new DBUploadAsThread(files[i]);
            threads[i] = new Thread(job);
            threads[i].start();
        }

        try {
            for (int i = 0; i < threads.length; i++){
                threads[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("Unable to join threads from upload.");
        }

        uploadForeignKeys(files);

        System.out.println();
        System.out.println("All jobs are completed.... ");
    }

    /**
     * Alter tables and uploads foreign keys from text file after thread has run.
     * @param files
     * @throws CustomFileNotFoundException
     * @throws CustomIOException
     * @throws CustomSQLException
     */
    public void uploadForeignKeys(String[] files) throws CustomFileNotFoundException, CustomIOException, CustomSQLException {
        DBValidationHandler handler = new DBValidationHandler();

        for (int i = 0; i < files.length; i++)
            handler.fixForeignKeysForTable(files[i]);

    }

    /**
     * Retrieves all files from input directory
     * @return
     */
    public String[] getAllFiles() {
        File folder = new File("input/");
        File[] orgFile = folder.listFiles();
        String[] files = new String[orgFile.length];
        int n = 0;

        for (int i = 0; i < orgFile.length; i++) {
            if (orgFile[i].isFile()) {
                files[n] = orgFile[i].getName();
                n++;
            }
        }

        return trimStringArray(files);
    }

    /**
     * Removes null from array of files.
     * @param array
     * @return
     */
    private String[] trimStringArray(String[] array){
        ArrayList<String> list = new ArrayList<>();

        for(String s : array) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }

        return list.toArray(new String[list.size()]);
    }
}
