package tree.me.sample.service.elastic;

public class ElasticPageMappings {

    public static final String MAPPING = "{  \n" +
            "   \"page\":{  \n" +
            "      \"properties\":{  \n" +
            "         \"id\":{  \n" +
            "            \"type\":\"string\",\n" +
            "            \"index\":\"not_analyzed\",\n" +
            "            \"doc_values\":false\n" +
            "         },\n" +
            "         \"name\":{  \n" +
            "            \"type\":\"string\"\n" +
            "         },\n" +
            "         \"tags\":{  \n" +
            "            \"type\":\"string\",\n" +
            "            \"index\":\"not_analyzed\",\n" +
            "            \"doc_values\":true\n" +
            "         },\n" +
            "         \"markdown\":{  \n" +
            "            \"type\":\"string\",\n" +
            "            \"index\":\"not_analyzed\",\n" +
            "            \"doc_values\":false\n" +
            "         },\n" +
            "         \"enrichment\":{  \n" +
            "            \"properties\":{  \n" +
            "               \"key\":{  \n" +
            "                  \"type\":\"string\",\n" +
            "                  \"index\":\"not_analyzed\",\n" +
            "                  \"doc_values\":false\n" +
            "               },\n" +
            "               \"value\":{  \n" +
            "                  \"type\":\"string\",\n" +
            "                  \"index\":\"not_analyzed\",\n" +
            "                  \"doc_values\":false\n" +
            "               },\n" +
            "               \"source\":{  \n" +
            "                  \"type\":\"string\",\n" +
            "                  \"index\":\"not_analyzed\",\n" +
            "                  \"doc_values\":false\n" +
            "               }\n" +
            "            }\n" +
            "         }\n" +
            "      }\n" +
            "   }\n" +
            "}";
}
