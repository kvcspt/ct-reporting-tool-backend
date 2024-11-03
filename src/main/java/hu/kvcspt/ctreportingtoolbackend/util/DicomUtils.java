package hu.kvcspt.ctreportingtoolbackend.util;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.io.DicomInputStream;

import java.io.IOException;
import java.io.InputStream;

public class DicomUtils {

    public static Attributes parseDicom(InputStream inputStream){
        try (DicomInputStream dis = new DicomInputStream(inputStream)) {
            return dis.readDataset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
