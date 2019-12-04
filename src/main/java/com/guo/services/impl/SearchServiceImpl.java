package com.guo.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.guo.base.HouseSort;
import com.guo.base.RentValueBlock;
import com.guo.dto.HouseBucketDTO;
import com.guo.entity.*;
import com.guo.form.MapSearch;
import com.guo.form.RentSearch;
import com.guo.repository.HouseDetailRepository;
import com.guo.repository.HouseRepository;
import com.guo.repository.HouseTagRepository;
import com.guo.repository.SupportAddressRepository;
import com.guo.services.AddressService;
import com.guo.services.ISearchService;
import com.guo.services.ServiceMultiResult;
import com.guo.services.ServiceResult;


import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @desception:
 * @author: mi
 * @date: 2019-08-20 9:50
 */
@Service
public class SearchServiceImpl implements ISearchService {

  private static final Logger logger = LoggerFactory.getLogger(ISearchService.class);

  private static final String INDEX_NAME = "zufang";

  private static final String INDEX_TYPE = "house";

  private static final String INDEX_TOPIC = "house_build";

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private HouseRepository houseRepository;

  @Autowired
  private HouseDetailRepository houseDetailRepository;

  @Autowired
  private HouseTagRepository tagRepository;

  @Autowired
  private TransportClient esClient;

  @Autowired
  private SupportAddressRepository supportAddressRepository;

  @Autowired
  private AddressService addressService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public void index(Long houseId) {

    this.index(houseId, 0);
    /*House house = houseRepository.findOne(houseId);
    if(house == null){
      logger.error("Index house {} does not exit!!!",houseId);
      return false;
    }

    HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
    modelMapper.map(house,indexTemplate);

    HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
    if(detail == null){
      // TODD 异常情况
    }
    modelMapper.map(detail,indexTemplate);

    List<HouseTag>  tags = tagRepository.findAllByHouseId(houseId);
    if(tags != null && !tags.isEmpty()){
      List<String> tagStrings = new ArrayList<>();
      tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
      indexTemplate.setTags(tagStrings);
    }

    SearchRequestBuilder requestBuilder =
    this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId));

    logger.debug(requestBuilder.toString());

    SearchResponse searchResponse = requestBuilder.execute().actionGet();

    boolean success;

    Long totalHit = searchResponse.getHits().getTotalHits();
    if(totalHit == 0){
      success = create(indexTemplate);
    }else if(totalHit == 1){
      String esId = searchResponse.getHits().getAt(0).getId();
      success = update(esId,indexTemplate);
    }else{
      success = deleteAndCreate(totalHit,indexTemplate);
    }

    if(success){
      logger.debug("index success with house " + houseId);
    }

    return success;*/
    // create

    // update

    // delete & create

    //houseDetailRepository.findByHouseId(houseId);
  }

  private void index(Long houseId, int retry) {

    if (retry > HouseIndexMessage.MAX_RETRY) {
      logger.error("retry index times over 3 for house" + houseId + " please check it !!!");
    }

    HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.INDEX, retry);
    try {
      kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      logger.error("json encode error for " + message);
    }

  }


  private boolean create(HouseIndexTemplate indexTemplate) {
    if (!updateSuggest(indexTemplate)) {
      return false;
    }
    try {
      IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
              .setSource(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

      logger.debug("Create index with house: " + indexTemplate.getHouseId());
      if (response.status() == RestStatus.OK) {
        return true;
      } else {
        return false;
      }
    } catch (JsonProcessingException e) {
      logger.error("Error to index house " + indexTemplate.getHouseId(), e);
      return false;
    }
  }

  private boolean update(String esId, HouseIndexTemplate indexTemplate) {
    if (!updateSuggest(indexTemplate)) {
      return false;
    }

    try {
      UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
              .setDoc(objectMapper.writeValueAsBytes(indexTemplate), XContentType.JSON).get();

      logger.debug("Update index with house: " + indexTemplate.getHouseId());
      if (response.status() == RestStatus.OK) {
        return true;
      } else {
        return false;
      }
    } catch (JsonProcessingException e) {
      logger.error("Error to index house " + indexTemplate.getHouseId(), e);
      return false;
    }
  }


  @Override
  public void remove(Long houseId) {
    /*DeleteByQueryRequestBuilder builder =
            DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient).filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID,houseId)).source(INDEX_NAME);

    logger.debug("Delete by query for house :" + builder);

    BulkByScrollResponse response = builder.get();
    Long deleted = response.getDeleted();
    logger.debug("delete total" + deleted);*/
    this.remove(houseId, 0);

  }

  private void remove(Long houseId, int retry) {
    if (retry > HouseIndexMessage.MAX_RETRY) {
      logger.error("retry index times over 3 for house" + houseId + " please check it !!!");
    }

    HouseIndexMessage message = new HouseIndexMessage(houseId, HouseIndexMessage.REMOVE, retry);
    try {

      kafkaTemplate.send(INDEX_TOPIC, objectMapper.writeValueAsString(message));
    } catch (JsonProcessingException e) {
      logger.error("Cannot encode json for " + message, e);
    }

  }

  private boolean deleteAndCreate(Long totalHit, HouseIndexTemplate indexTemplate) {
    DeleteByQueryRequestBuilder builder =
            DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient).filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, indexTemplate.getHouseId())).source(INDEX_NAME);

    logger.debug("Delete by query for house :" + builder);

    BulkByScrollResponse response = builder.get();
    Long deleted = response.getDeleted();
    if (deleted != totalHit) {
      logger.warn("Need delete {} ,but {} was delete !!! ", totalHit, deleted);
      return false;
    } else {
      return create(indexTemplate);
    }
  }

  // 查询搜索执行
  @Override
  public ServiceMultiResult<Long> query(RentSearch rentSearch) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

    // es查询条件的构建:这里介绍两种查询条件的构建，must表示且，should表示或；
    //  同时用到了两种匹配方式：termQuery代表完全匹配，配合索引类型 keywords 使用，matchQuery代表模糊匹配，配合text使用。
    boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, rentSearch.getCityEnName()));  //  城市搜索

    if (rentSearch.getRegionEnName() != null && !"*".equals(rentSearch.getRegionEnName())) {
      boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, rentSearch.getRegionEnName()));  //区域搜索
    }

    if (rentSearch.getRentWay() != -1) {    //  此处对租赁方式进行校验
      boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay()));
    }

    RentValueBlock area = RentValueBlock.matchArea(rentSearch.getAreaBlock());
    if (!RentValueBlock.ALL.equals(area)) {
      RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(HouseIndexKey.AREA); //RangeQueryBuilder 构建范围查询条件
      if (area.getMax() > 0) {
        rangeQueryBuilder.lte(area.getMax());
      }
      if (area.getMin() > 0) {
        rangeQueryBuilder.gte(area.getMin());
      }
      boolQuery.filter(rangeQueryBuilder);
    }

    RentValueBlock price = RentValueBlock.matchPrice(rentSearch.getPriceBlock());
    if (!RentValueBlock.ALL.equals(price)) {
      RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(HouseIndexKey.AREA); //RangeQueryBuilder 构建范围查询条件
      if (price.getMax() > 0) {
        rangeQuery.lte(price.getMax());
      }
      if (price.getMin() > 0) {
        rangeQuery.gte(price.getMin());
      }
      boolQuery.filter(rangeQuery);
    }

    if (rentSearch.getDirection() > 0) {
      boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.DESCRIPTION, rentSearch.getDirection()));
    }

    if (rentSearch.getRentWay() > -1) {
      boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.RENT_WAY, rentSearch.getRentWay()));

    }

    // boolQuery.should(QueryBuilders.matchQuery(HouseIndexKey.TITLE,rentSearch.getKeywords()).boost(2.0f));  // 加权重
    if (!StringUtils.isEmpty(rentSearch.getKeywords())) {


      boolQuery.must(QueryBuilders.multiMatchQuery(rentSearch.getKeywords(),
              HouseIndexKey.TITLE,     //主题
              HouseIndexKey.TRAFFIC,    // traffic  交通出行
              HouseIndexKey.DISTRICT,    //  所在小区
              HouseIndexKey.ROUND_SERVICE,   // round_service 周边配套
              HouseIndexKey.SUBWAY_LINE_NAME,  // 地铁线路
              HouseIndexKey.SUBWAY_STATION_NAME  // 地铁站名
      ));
    }

    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(boolQuery)
            .addSort(
                    HouseSort.getSortKey(rentSearch.getOrderBy()),
                    SortOrder.fromString(rentSearch.getOrderDirection())
            )
            .setFrom(rentSearch.getStart())
            .setSize(rentSearch.getSize())
            .setFetchSource(HouseIndexKey.HOUSE_ID, null);

    logger.debug(requestBuilder.toString());

    List<Long> houseIds = new ArrayList<>();
    SearchResponse response = requestBuilder.get();
    if (response.status() != RestStatus.OK) {
      logger.warn("Search status is no ok for" + requestBuilder);
      return new ServiceMultiResult<>(0, houseIds);
    }

    for (SearchHit hit : response.getHits()) {
      houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
    }

    return new ServiceMultiResult<>(response.getHits().totalHits, houseIds);
  }

  //   分词
  private boolean updateSuggest(HouseIndexTemplate indexTemplate) {
    AnalyzeRequestBuilder requestBuilder = new AnalyzeRequestBuilder(this.esClient
            , AnalyzeAction.INSTANCE, INDEX_NAME,
            indexTemplate.getTitle(),
            indexTemplate.getLayoutDesc(),
            indexTemplate.getRoundService(),
            indexTemplate.getDescription(),
            indexTemplate.getSubwayLineName(),
            indexTemplate.getSubwayStationName());

    requestBuilder.setAnalyzer("ik_smart");

    AnalyzeResponse response = requestBuilder.get();
    List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
    if (tokens == null) {
      logger.warn("Can not analyze token for house:" + indexTemplate.getHouseId());
      return false;
    }

    List<HouseSuggest> suggests = new ArrayList<>();
    for (AnalyzeResponse.AnalyzeToken token : tokens) {
      // 排序数字类型 & 小于2个字符的分词结果
      if ("<NUM>".equals(token.getType()) || token.getTerm().length() < 2) {
        continue;
      }
      HouseSuggest suggest = new HouseSuggest();
      suggest.setInput(token.getTerm());
      suggests.add(suggest);
    }

    // 定制化小区自动补全
    HouseSuggest suggest = new HouseSuggest();
    suggest.setInput(indexTemplate.getDistrict());
    // suggest.setInput(indexTemplate.getTitle());
    suggests.add(suggest);

    indexTemplate.setSuggest(suggests);
    return true;
  }

  @Override
  public ServiceResult<List<String>> suggest(String prefix) {
    CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("suggest").prefix(prefix).size(5);

    SuggestBuilder suggestBuilder = new SuggestBuilder();
    suggestBuilder.addSuggestion("autocomplete", suggestion);

    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).suggest(suggestBuilder);
    logger.debug(requestBuilder.toString());

    SearchResponse response = requestBuilder.get();
    Suggest suggest = response.getSuggest();
    if (suggest == null) {
      return ServiceResult.of(new ArrayList<>());
    }

    Suggest.Suggestion result = suggest.getSuggestion("autocomplete");

    int maxSuggest = 0;
    Set<String> suggestSet = new HashSet<>();

    for (Object term : result.getEntries()) {
      if (term instanceof CompletionSuggestion.Entry) {
        CompletionSuggestion.Entry item = (CompletionSuggestion.Entry) term;
        if (item.getOptions().isEmpty()) {
          continue;
        }
        for (CompletionSuggestion.Entry.Option option : item.getOptions()) {
          String tip = option.getText().string();
          if (suggestSet.contains(tip)) {
            continue;
          }
          suggestSet.add(tip);
          maxSuggest++;
        }
      }
      if (maxSuggest > 5) {
        break;
      }
    }

    List<String> suggests = Lists.newArrayList(suggestSet.toArray(new String[]{}));
    return ServiceResult.of(suggests);
  }

  @Override    // 聚合特定小区的房间数
  public ServiceResult<Long> aggregateDistrictHouse(String cityEnName, String regionEnName, String district) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
            .filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName))
            .filter(QueryBuilders.termQuery(HouseIndexKey.REGION_EN_NAME, regionEnName))
            .filter(QueryBuilders.termQuery(HouseIndexKey.DISTRICT, district));

    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(boolQuery)
            .addAggregation(AggregationBuilders.terms(HouseIndexKey.AGG_DISTRICT).field(HouseIndexKey.DISTRICT)).setSize(0);

    logger.debug(requestBuilder.toString());

    SearchResponse response = requestBuilder.get();
    if (response.status() == RestStatus.OK) {
      Terms terms = response.getAggregations().get(HouseIndexKey.AGG_DISTRICT);
      if (terms.getBuckets() != null && !terms.getBuckets().isEmpty()) {
        return ServiceResult.of(terms.getBucketByKey(district).getDocCount());
      }
    } else {
      logger.warn("failed to Aggregate for" + HouseIndexKey.AGG_DISTRICT);

    }
    return ServiceResult.of(0L);
  }

  //    聚合城市数
  @Override
  public ServiceMultiResult<HouseBucketDTO> mapAggregate(String cityEnName) {

    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));

    AggregationBuilder aggBuilder = AggregationBuilders.terms(HouseIndexKey.AGG_REGION).field(HouseIndexKey.REGION_EN_NAME);
    SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(boolQuery).addAggregation(aggBuilder);

    logger.debug(requestBuilder.toString());

    SearchResponse response = requestBuilder.get();
    List<HouseBucketDTO> buckets = new ArrayList<>();
    if (response.status() != RestStatus.OK) {
      logger.warn("Aggregate status is not ok for " + requestBuilder);
      return new ServiceMultiResult<>(0, buckets);
    }

    Terms terms = response.getAggregations().get(HouseIndexKey.AGG_REGION);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      buckets.add(new HouseBucketDTO(bucket.getKeyAsString(), bucket.getDocCount()));
    }

    return new ServiceMultiResult<>(response.getHits().getTotalHits(), buckets);
  }

  //城市级别查询
  @Override
  public ServiceMultiResult<Long> mapQuery(String cityEnName, String orderBy,
                                           String orderDirection,
                                           int start,
                                           int size) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, cityEnName));

    SearchRequestBuilder searchRequestBuilder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(boolQuery)
            .addSort(HouseSort.getSortKey(orderBy), SortOrder.fromString(orderDirection))
            .setFrom(start)
            .setSize(size);

    List<Long> houseIds = new ArrayList<>();
    SearchResponse response = searchRequestBuilder.get();
    if (response.status() != RestStatus.OK) {
      logger.warn("Search status is not ok for " + searchRequestBuilder);
      return new ServiceMultiResult<>(0, houseIds);
    }

    for (SearchHit hit : response.getHits()) {
      houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
    }
    return new ServiceMultiResult<>(response.getHits().getTotalHits(), houseIds);
  }

  //精确范围数据查询
  @Override
  public ServiceMultiResult<Long> mapQuery(MapSearch mapSearch) {
    BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
    boolQuery.filter(QueryBuilders.termQuery(HouseIndexKey.CITY_EN_NAME, mapSearch.getCityEnName()));

    boolQuery.filter(
            QueryBuilders.geoBoundingBoxQuery("location")
                    .setCorners(
                            new GeoPoint(mapSearch.getLeftLatitude(), mapSearch.getLeftLongitude()),
                            new GeoPoint(mapSearch.getRightLatitude(), mapSearch.getRightLongitude())
                    ));

    SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME)
            .setTypes(INDEX_TYPE)
            .setQuery(boolQuery)
            .addSort(HouseSort.getSortKey(mapSearch.getOrderBy()),
                    SortOrder.fromString(mapSearch.getOrderDirection()))
            .setFrom(mapSearch.getStart())
            .setSize(mapSearch.getSize());

    List<Long> houseIds = new ArrayList<>();
    SearchResponse response = builder.get();
    if (RestStatus.OK != response.status()) {
      logger.warn("Search status is not ok for " + builder);
      return new ServiceMultiResult<>(0, houseIds);
    }

    for (SearchHit hit : response.getHits()) {
      houseIds.add(Longs.tryParse(String.valueOf(hit.getSourceAsMap().get(HouseIndexKey.HOUSE_ID))));
    }
    return new ServiceMultiResult<>(response.getHits().getTotalHits(), houseIds);
  }

  @KafkaListener(topics = INDEX_TOPIC)
  private void handleMessage(String content) {

    try {
      HouseIndexMessage message = objectMapper.readValue(content, HouseIndexMessage.class);

      switch (message.getOperation()) {
        case HouseIndexMessage.INDEX:
          this.createOrUpdateIndex(message);
          break;
        case HouseIndexMessage.REMOVE:
          this.removeIndex(message);
          break;
        default:
          logger.warn("Not support message content " + content);
          break;
      }
    } catch (IOException e) {
      logger.error("can not parse json for " + content, e);
    }
  }

  private void createOrUpdateIndex(HouseIndexMessage message) {

    Long houseId = message.getHouseId();

    House house = houseRepository.findOne(houseId);
    if (house == null) {
      logger.error("Index house {} does not exit!!!", houseId);
      this.index(houseId, message.getRetry() + 1);     //该出有重写index方法 index(Long houseId,int retry)
      return;
    }

    HouseIndexTemplate indexTemplate = new HouseIndexTemplate();
    modelMapper.map(house, indexTemplate);

    HouseDetail detail = houseDetailRepository.findByHouseId(houseId);
    if (detail == null) {
      // TODD 异常情况
    }
    modelMapper.map(detail, indexTemplate);

    /*SupportAddress city = supportAddressRepository.findByEnNameAndLevel(house.getCityEnName(), SupportAddress.Level.CITY.getValue()); //
    SupportAddress region = supportAddressRepository.findByEnNameAndLevel(house.getRegionEnName(), SupportAddress.Level.REGION.getValue());
    String address = city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict() + detail.getDetailAddress();
    ServiceResult<BaiduMapLocation> location = addressService.getBaiduMapLocation(city.getCnName(), address);
    if (!location.isSuccess()) {
      this.index(message.getHouseId(), message.getRetry() + 1);
      return;
    }
    indexTemplate.setLocation(location.getResult());  //*/

    List<HouseTag> tags = tagRepository.findAllByHouseId(houseId);
    if (tags != null && !tags.isEmpty()) {
      List<String> tagStrings = new ArrayList<>();
      tags.forEach(houseTag -> tagStrings.add(houseTag.getName()));
      indexTemplate.setTags(tagStrings);
    }

    SearchRequestBuilder requestBuilder =
            this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));

    logger.debug(requestBuilder.toString());

    SearchResponse searchResponse = requestBuilder.execute().actionGet();

    boolean success;

    Long totalHit = searchResponse.getHits().getTotalHits();
    if (totalHit == 0) {
      success = create(indexTemplate);
    } else if (totalHit == 1) {
      String esId = searchResponse.getHits().getAt(0).getId();
      success = update(esId, indexTemplate);
    } else {
      success = deleteAndCreate(totalHit, indexTemplate);
    }

    /*ServiceResult serviceResult = addressService.lbsUpload(location.getResult(), house.getStreet() + house.getDistrict(),
            city.getCnName() + region.getCnName() + house.getStreet() + house.getDistrict(),
            message.getHouseId(), house.getPrice(), house.getArea());

    if (!success || !serviceResult.isSuccess()) {
      this.index(message.getHouseId(), message.getRetry() + 1);
    } else {
      logger.debug("Index success with house " + houseId);

    }*/

    if (success) {
      logger.debug("index success with house " + houseId);
    }
  }

  private void removeIndex(HouseIndexMessage message) {

    Long houseId = message.getHouseId();

    // es中查询不到的话 再进行删除
    SearchRequestBuilder requestBuilder =
            this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE).setQuery(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId));

    SearchResponse searchResponse = requestBuilder.execute().actionGet();//

    if (searchResponse.status() != RestStatus.OK) {
      this.remove(houseId, message.getRetry() + 1);
    } else {
      long totalHits = searchResponse.getHits().totalHits;
      if (totalHits == 0) {
        return;
      }
      DeleteByQueryRequestBuilder builder =
              DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient).filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId)).source(INDEX_NAME);

      logger.debug("Delete by query for house :" + builder);

      BulkByScrollResponse response = builder.get();
      Long deleted = response.getDeleted();
      logger.debug("delete total" + deleted);

      ServiceResult serviceResult = addressService.removeLbs(houseId);

      if (!serviceResult.isSuccess() || deleted <= 0) {
        logger.warn("did not remove data from es for response: " + response);
        // 重新加入消息队列
        this.remove(houseId, message.getRetry() + 1);
      }
    }

    /*Long houseId = message.getHouseId();
    DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
            .newRequestBuilder(esClient)
            .filter(QueryBuilders.termQuery(HouseIndexKey.HOUSE_ID, houseId))
            .source(INDEX_NAME);

    logger.debug("Delete by query for house: " + builder);

    BulkByScrollResponse response = builder.get();
    long deleted = response.getDeleted();
    logger.debug("Delete total " + deleted);

    ServiceResult serviceResult = addressService.removeLbs(houseId);

    if (!serviceResult.isSuccess() || deleted <= 0) {
      logger.warn("Did not remove data from es for response: " + response);
      // 重新加入消息队列
      this.remove(houseId, message.getRetry() + 1);*/

  }


}
