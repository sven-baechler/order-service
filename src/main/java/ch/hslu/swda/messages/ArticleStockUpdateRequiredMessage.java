package ch.hslu.swda.messages;

public record ArticleStockUpdateRequiredMessage(String articleId, long amount) {

}
