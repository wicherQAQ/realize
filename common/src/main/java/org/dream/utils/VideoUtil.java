package org.dream.utils;

import org.dream.exception.VideoMeasureException;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频处理类
 * @author wwc
 */
public class VideoUtil {

    enum MType{
        /**
         * 以小时为单位
         */
        H,
        /**
         * 以分钟为单位
         */
        M,
        /**
         * 以秒为单位
         */
        S
    }

    private static String path;
    private static String file_suffix;
    private static String file_prefix;


    public static VideoUtil config(String filePath, String suffix) {
        path = filePath;
        file_suffix = suffix;
        //file_prefix = prefix;
        return new VideoUtil();
    }

    public static void main(String[] args) {
        String path = "C:/Users/X1 Carbon/Desktop/2020考研资料/高数/高数真题讲解/test";
        try {
            VideoUtil.config(path,"mp4")
                    .MeasureTime(MType.H);
        } catch (EncoderException e) {
            new VideoMeasureException("视频解码异常").printStackTrace();
        }
    }

    /**
     * 对文件进行批量重命名
     * 对文件名末端追加数字
     * @param newName 新文件名
     * @return
     */
    public VideoUtil rename(String newName){
        int[] count = {1};
        getFileList(new File(path),file_suffix).stream().forEach(targetFile->{
            //获取文件名
            String oldName = targetFile.getName();
            //获取文件后缀
            String suffix = oldName.substring(targetFile.getName().lastIndexOf("."));
            String name = newName+"_"+(count[0]++)+suffix;
            File renameFile = new File(targetFile.getParent() + File.separatorChar + name);
            targetFile.renameTo(renameFile);
        });
        System.out.println("modify successfully ##√##");
        return this;
    }

    /**
     * 计算视频总时长
     * @param measureType   H-hour M-minute S-second
     * @return
     * @throws EncoderException
     */
    public VideoUtil MeasureTime(MType measureType) throws EncoderException {
        //所有视频的总时长(unit ms)单位为毫秒
        BigDecimal totalTime =new BigDecimal(0);
        //毫秒转为秒的转换单位
        BigDecimal transferMSUnit = new BigDecimal(1000);
        //秒转为分钟的转换单位
        BigDecimal transferSUnit = new BigDecimal(60);
        //分钟转为小时的转换单位
        BigDecimal transferHUnit = new BigDecimal(3600);
        File file = new File(path);
        List<File> fileList = getFileList(file,file_suffix);
        System.out.println("=========总视频数目为" + fileList.size() + "==========");
        for (File targetFile : fileList) {
            MultimediaObject mmObject= new MultimediaObject(targetFile);
            MultimediaInfo media= mmObject.getInfo();
            BigDecimal time =new BigDecimal(media.getDuration());
            //unit is second（单位为秒）
            time = time.divide(transferMSUnit,2,BigDecimal.ROUND_HALF_UP);
            totalTime = totalTime.add(time);
        }

        switch (measureType){
            case H:
                BigDecimal hourTime = totalTime.divide(transferHUnit,2, BigDecimal.ROUND_HALF_UP);
                System.out.println("=========总视频时长为" + hourTime + "小时=========");
                break;
            case M:
                BigDecimal minuteTime = totalTime.divide(transferSUnit,2, BigDecimal.ROUND_HALF_UP);
                System.out.println("=========总视频时长为" + minuteTime + "分钟=======");
                break;
            case S:
                System.out.println("=========总视频时长为" + totalTime + "秒======");
                break;
            default:
                minuteTime = totalTime.divide(transferSUnit,2, BigDecimal.ROUND_HALF_UP);
                System.out.println("=========总视频时长为" + minuteTime + "分钟=======");
                break;
        }
        return this;
    }

    /**
     * 获取文件夹下所有的mp4文件
     * 递归调用
     * @param file
     * @return
     */
    public List<File> getFileList(File file,String suffix) {
        List<File> fileList = new ArrayList<File>();
        if (file != null) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                //判断是否为文件夹
                if (files[i].isDirectory()) {
                    fileList.addAll(getFileList(files[i],suffix));
                } else if (files[i].getName().endsWith(suffix)) {
                    fileList.add(files[i]);
                }
            }
        }
        return fileList;
    }

}
