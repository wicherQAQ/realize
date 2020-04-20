package org.dream.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件
 * @@author wwc
 */
@Getter
@Setter
public class Attachment {
    /**
     * 说明
     */
    private String describe;

    /**
     * 包含文件
     */
    private List<File> attachFileList = new ArrayList<File>();


    /**
     * 自定义文件名
     */
    private List<String> attachFileNameList = new ArrayList<String>();

    /**
     * 检测文件是否合理
     * 每个文件对应一个文件名
     * @return
     */
    public Boolean valid(){
        return attachFileList.size()==attachFileNameList.size();
    }

}
