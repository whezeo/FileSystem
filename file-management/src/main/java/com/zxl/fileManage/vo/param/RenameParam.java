package com.zxl.fileManage.vo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenameParam {

    String fullPath;
    String newName;
}
