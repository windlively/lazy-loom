package ink.windlively.fileserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class FileModel {

    private String reqPath;

    private String name;

    private boolean dir;

    private Date lastModified;

    private long spaces;
}
