import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;


class Download extends Observable  {

    private static final int MAX_BUFFER_SIZE = 1024;
    public static final String STATUSES[] = {"Downloading",
            "Paused", "Complete", "Cancelled", "Error"};
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private URL url;
    private int size;
    private int downloaded;
    private int status;
    public Download(URL url) {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        download();
    }

    public String getUrl() {
        return url.toString();
    }

    public int getSize() {
        return size;
    }

    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    public int getStatus() {
        return status;
    }

    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

    private void error() {
        status = ERROR;
        stateChanged();
    }

    private void download() {
        Thread thread = new Thread(() ->{
            RandomAccessFile file = null;
            InputStream stream = null;

            try {
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range",
                        "bytes=" + downloaded + "-");
                connection.connect();
                if (connection.getResponseCode() / 100 != 2) {
                    error();
                }
                int contentLength = connection.getContentLength();
                if (contentLength < 1) {
                    error();
                }


                if (size == -1) {
                    size = contentLength;
                    stateChanged();
                }
                file = new RandomAccessFile(getFileName(url), "rw");
                file.seek(downloaded);

                stream = connection.getInputStream();
                while (status == DOWNLOADING) {

                    byte buffer[];
                    if (size - downloaded > MAX_BUFFER_SIZE) {
                        buffer = new byte[MAX_BUFFER_SIZE];
                    } else {
                        buffer = new byte[size - downloaded];
                    }

                    int read = stream.read(buffer);
                    if (read == -1)
                        break;


                    file.write(buffer, 0, read);
                    downloaded += read;
                    stateChanged();
                }


                if (status == DOWNLOADING) {
                    status = COMPLETE;
                    stateChanged();
                    size=(int)size/(1024*1024);

                }
            } catch (Exception e) {
                error();
            } finally {

                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {}
                }


                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {}
                }
            }
        });
        thread.setName("downloading");
        thread.start();
    }

    private String getFileName(URL url) {
        String fileName = url.getFile();
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }



    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}

class ProgressRenderer extends JProgressBar
        implements TableCellRenderer {
    public ProgressRenderer(int min, int max) {
        super(min, max);
    }
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        setValue((int) ((Float) value).floatValue());
        return this;
    }
}

class DownloadsTableModel extends AbstractTableModel implements Observer {
    private static final String[] columnNames = {"URL", "Size","Progress", "Status"};

    private static final Class[] columnClasses = {String.class,
            String.class, JProgressBar.class, String.class};

    private ArrayList downloadList = new ArrayList();

    public void addDownload(Download download) {
        download.addObserver(this);

        downloadList.add(download);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }
    public Download getDownload(int row) {
        return (Download) downloadList.get(row);
    }

    public void clearDownload(int row) {
        downloadList.remove(row);

        fireTableRowsDeleted(row, row);
    }

    public int getColumnCount() {
        return columnNames.length;
    }
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    public int getRowCount() {
        return downloadList.size();
    }

    public Object getValueAt(int row, int col) {

        Download download = (Download) downloadList.get(row);
        switch (col) {
            case 0:
                return download.getUrl();
            case 1:
                int size = download.getSize();
                return (size == -1) ? "" : Integer.toString(size);
            case 2:
                return new Float(download.getProgress());
            case 3:
                return Download.STATUSES[download.getStatus()];
        }
        return "";
    }
    public void update(Observable o, Object arg) {
        int index = downloadList.indexOf(o);
        fireTableRowsUpdated(index, index);
    }
}
