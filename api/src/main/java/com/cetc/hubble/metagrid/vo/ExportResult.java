package com.cetc.hubble.metagrid.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ExportResult",description = "导出的请求结果")
public class ExportResult {
    public static enum Result{
        success,error,doing;
    }
    @ApiModelProperty(notes = "数据下载的路径", example = "/downlaod/apc.zip")
    private String path;
    @ApiModelProperty(notes = "导出解结果,成功：success，失败：error，正在处理：doing", example = "success")
    private String result = Result.success.name() ;
    @ApiModelProperty(notes = "导出结果信息，导出失败后的信息会在此字段中", example = "导出成功")
    private String msg  = "success";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
