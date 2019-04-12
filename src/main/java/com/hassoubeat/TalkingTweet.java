/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hassoubeat;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


/**
 * 指定した条件に合致するツイートを読み上げるファセット
 * @author hassoubeat
 */
public class TalkingTweet implements FacetInterface{
    
    private PropertyUtil propertyUtil = PropertyUtil.getInstace();
    private final String PROPERTIES_FILE_NAME = "TalkingTweet.properties";
//    private final String PROPERTIES_FILE_NAME = "TalkingTweet";
//    private ResourceBundle properties = ResourceBundle.getBundle(PROPERTIES_FILE_NAME); 
    
    SimpleDateFormat tweetSdf = new SimpleDateFormat("MM月dd日HH時mm分");
    
//    private final String SEARCH_WORD_LIST_STR = "@fgoproject,#fgo";
//    private final int FETCH_COUNT = 10;
//    private final boolean IS_FETCH_RETWEET = false;
//    private final String CONSUMER_KEY =  properties.getString("consumerKey");
//    private final String CONSUMER_SECRET =  properties.getString("consumerSecret");
//    private final String ACCESS_TOKEN =  properties.getString("accessToken");
//    private final String ACCESS_TOKEN_SECRET =  properties.getString("accessTokenSecret");
//    private final String BEFORE_FETCH_TWEET_MAX_ID = "";
    private final String SEARCH_WORD_LIST_STR;
    private int FETCH_COUNT;
    private boolean IS_FETCH_RETWEET;
    private long BEFORE_FETCH_TWEET_MAX_ID;
    private final String CONSUMER_KEY;
    private final String CONSUMER_SECRET;
    private final String ACCESS_TOKEN;
    private final String ACCESS_TOKEN_SECRET;
    
    
    public TalkingTweet() {
        SEARCH_WORD_LIST_STR = propertyUtil.load(PROPERTIES_FILE_NAME, "search.word.list.str");
        try {
            FETCH_COUNT = Integer.parseInt(propertyUtil.load(PROPERTIES_FILE_NAME, "tweet.fetch.count"));
        } catch (NumberFormatException ex) {
            FETCH_COUNT = 5;
        }
        try {
            IS_FETCH_RETWEET = Boolean.valueOf(propertyUtil.load(PROPERTIES_FILE_NAME, "is.fetch.retweet"));
        } catch (NullPointerException ex) {
            IS_FETCH_RETWEET = false;
        }
        try {
            BEFORE_FETCH_TWEET_MAX_ID = Long.parseLong(propertyUtil.load(PROPERTIES_FILE_NAME, "before.fetch.tweet.max.id"));
        } catch (NumberFormatException ex) {
            BEFORE_FETCH_TWEET_MAX_ID = 0;
        }
        CONSUMER_KEY =  propertyUtil.load(PROPERTIES_FILE_NAME, "consumer.key");
        CONSUMER_SECRET =  propertyUtil.load(PROPERTIES_FILE_NAME, "consumer.secret");
        ACCESS_TOKEN =  propertyUtil.load(PROPERTIES_FILE_NAME, "access.token");
        ACCESS_TOKEN_SECRET =  propertyUtil.load(PROPERTIES_FILE_NAME, "access.token.secret");
    }
    
    
    @Override
    public Result execute() throws Exception {
        Result result = new Result();
        String talking = "";
        
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey(CONSUMER_KEY)
        .setOAuthConsumerSecret(CONSUMER_SECRET)
        .setOAuthAccessToken(ACCESS_TOKEN)
        .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        Twitter twitter = new TwitterFactory(cb.build()).getInstance();
        
        // 検索ワードの確認
        if (StringUtils.isEmpty(SEARCH_WORD_LIST_STR)) {
            System.out.println("トーキングツイートファセットからの通知です。検索ワードが指定されていません。");
            talking += "トーキングツイートファセットからの通知です。検索ワードが指定されていません。";
            ToyTalk.talking(talking, true);
            return result;
        }
        
        String[] searchWordList = SEARCH_WORD_LIST_STR.split(",");
        
        List<Status> tweetList = new ArrayList<>();

        for (int index = 0 ; searchWordList.length > index; index++) {
            String searchWord = searchWordList[index];
            Query query = new Query();
            if (searchWord.length() == 0) {
                // 空文字だった場合
                continue;
            } else {
                // 空文字じゃなかった場合
                if ("@".equals(searchWord.substring(0,1))) {
                    searchWord =  searchWord.replaceFirst("@", "from:");
                }
            }
            System.out.println(searchWord);
            query.setQuery(searchWord);
            query.setSinceId(BEFORE_FETCH_TWEET_MAX_ID);
            query.setCount(FETCH_COUNT);
            
            try {
                tweetList.addAll(twitter.search(query).getTweets());
            } catch (TwitterException ex) {
                System.out.println("トーキングツイートファセットからの通知です。ツィートの取得に失敗しました。設定を確認してください。");
                talking += "トーキングツイートファセットからの通知です。ツィートの取得に失敗しました。設定を確認してください。";
                ToyTalk.talking(talking, true);
                return result;
            }
        }
        

        
        List<Status> talkingTweetList = new ArrayList<>();
        if (IS_FETCH_RETWEET) {
            // リツイートを含む
            talkingTweetList = tweetList;
        } else {
            // リツイートを除外
            for (Status tweet : tweetList) {
                if (!tweet.isRetweet()) {
                    // リツイートじゃない場合のみ追加
                    talkingTweetList.add(tweet);
                }
            }
        }
        
        // 日付の降順にソート
        Collections.sort(talkingTweetList, new TweetDateComparator());
        
        
        
        if (talkingTweetList.size() == 0) {
            // 一件も対象のツィートが存在しなかった場合
            talking += "最新のツィートがありませんでした。";
            System.out.println(talking);
            ToyTalk.talking(talking, true);
            return result;
        } else {
            if (FETCH_COUNT <= talkingTweetList.size()) {
                // フェッチ上限件数よりもヒット数が多かった場合
                talking += "最新のツィートは" + FETCH_COUNT + "件です";
                System.out.println("実際のヒット数：" + talkingTweetList.size() + "件");
            } else {
                // ヒット数よりフェッチ上限件数の方が多かった場合
                talking += "最新のツィートは" + talkingTweetList.size() + "件です";
            }
            
            // 最新のツイートIDを保存し、次回の取得時にこのツィートIDより古いIDは取得しないようにする
            long tweetMaxId = BEFORE_FETCH_TWEET_MAX_ID;

            int fetchCount = 0;
            for (Status tweet : talkingTweetList) {
                if (tweetMaxId < tweet.getId()) {
                    // 一番最新のツイートIDを保持する
                    tweetMaxId = tweet.getId();
                }
                String tweetDate = tweetSdf.format(tweet.getCreatedAt());
//                if (tweetDate.substring(0, 1) = "0") {
//                    
//                }
                talking += tweetDate;
                // ハッシュタグ/URL/RT/ユーザ名の削除
                StringTokenizer sta = new StringTokenizer(tweet.getText().replace("\n", " ").replace("#", " #"), " ");
                while(sta.hasMoreTokens()) {
                    String wk = sta.nextToken();
                    if(wk.indexOf("#") == -1 && wk.indexOf("http") == -1 && wk.indexOf("RT") == -1 && wk.indexOf("@") == -1){
                        talking += wk;
                    }
                }
                
                // 読めない文字の変換と削除
                talking = talking.replace("\n", "");
                talking = talking.replace("\u0020", "");
                talking = talking.replace("#", "ハッシュタグ");
                talking = talking.replace("(", "");
                talking = talking.replace(")", "");
                System.out.println(talking);

                ToyTalk.talking(talking, true);
                talking = "";
                
                if (fetchCount >= FETCH_COUNT) {
                    // フェッチ上限に達した場合、ループを抜ける
                    break;
                }
            }
            // 最新のツイートIDをプロパティファイルに保存する
            propertyUtil.save(PROPERTIES_FILE_NAME, "before.fetch.tweet.max.id", String.valueOf(tweetMaxId));
        }
        
        
        return result;
    }
}
