package io.birdge.linker.support.odata.model;

public class QueryMapClass {

  private QueryMapClass(QMBuilder builder) {
    this.$top = builder.top;
    this.$skip = builder.$skip;
    this.$select = builder.$select;
    this.$count = builder.$count;
    this.$orderby = builder.$orderby;
    this.$filter = builder.$filter;
    this.$expand = builder.$expand;
    this.$search = builder.$search;
  }
  private String $top;
  private String $skip;
  private String $select;
  private String $count;
  private String $orderby;
  private String $filter;
  private String $expand;
  private String $search;

  public String toString(){
    StringBuilder sb = new StringBuilder(128);
    sb.append("top=").append(this.$top)
        .append(",skip=").append(this.$skip)
        .append(",elect=").append(this.$select)
        .append(",count=").append(this.$count)
        .append(",orderby=").append(this.$orderby)
        .append(",filter=").append(this.$filter)
        .append(",expand=").append(this.$expand)
        .append(",search=").append(this.$search);
    return sb.toString();
  }
  public static QMBuilder builder() {
    return new QMBuilder();
  }
  public String get$top(){
    return this.$top;
  }
  public String get$skip(){
    return this.$skip;
  }
  public String get$select(){
    return this.$select;
  }
  public String get$count(){
    return this.$count;
  }
  public String get$orderby(){
    return this.$orderby;
  }
  public String get$filter(){
    return this.$filter;
  }
  public String get$expand(){
    return this.$expand;
  }
  public String get$search(){
    return this.$search;
  }

  public static class QMBuilder {
    public QMBuilder(){
    };
    private String top;
    private String $skip;
    private String $select;
    private String $count;
    private String $orderby;
    private String $filter;
    private String $expand;
    private String $search;

    public QueryMapClass build() {
      return new QueryMapClass(this);
    }
    public QMBuilder top(String top) {
      this.top = top;
      return this;
    }
    public QMBuilder skip(String skip) {
      this.$skip = skip;
      return this;
    }
    public QMBuilder select(String select) {
      this.$select = select;
      return this;
    }
    public QMBuilder count(String count) {
      this.$count = count;
      return this;
    }
    public QMBuilder orderby(String orderby) {
      this.$orderby = orderby;
      return this;
    }
    public QMBuilder filter(String filter) {
      this.$filter = filter;
      return this;
    }
    public QMBuilder expand(String expand) {
      this.$expand = expand;
      return this;
    }
    public QMBuilder search(String search) {
      this.$search = search;
      return this;
    }
  }
}
