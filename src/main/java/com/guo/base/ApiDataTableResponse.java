package com.guo.base;

/**
 * @desception:dataTable响应结构
 * @author: mi
 * @date: 2019-08-19 10:49
 */
public class ApiDataTableResponse extends ApiResponse {

  private int draw;

  private long recordsTotal;

  private long recordsFiltered;

  public ApiDataTableResponse(ApiResponse.Status status) {
    this(status.getCode(), status.getStandardMessage(), null);
  }

  public ApiDataTableResponse(int code, String message, Object data) {
    super(code, message, data);
  }

  @Override
  public String toString() {
    return "ApiDataTableResponse{" +
            "draw=" + draw +
            ", recordsTotal=" + recordsTotal +
            ", recordsFiltered=" + recordsFiltered +
            '}';
  }

  public int getDraw() {
    return draw;
  }

  public void setDraw(int draw) {
    this.draw = draw;
  }

  public long getRecordsTotal() {
    return recordsTotal;
  }

  public void setRecordsTotal(long recordsTotal) {
    this.recordsTotal = recordsTotal;
  }

  public long getRecordsFiltered() {
    return recordsFiltered;
  }

  public void setRecordsFiltered(long recordsFiltered) {
    this.recordsFiltered = recordsFiltered;
  }
}
