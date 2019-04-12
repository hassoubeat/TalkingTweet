<script type="text/javascript">
    function editComplete () {
        $.toast({
            heading: "変更が完了しました。",
            showHideTransition: "slide",
            icon: "info",
            bgColor: "#375a7f",
            position: "bottom-right",
            stack: "1"
        });
    }
</script>
<?php
    $filename = "/usr/bin/toytalk/facet/properties/TalkingTweet.properties";
    $propertyArray = parse_ini_file($filename); 
    if($_SERVER["REQUEST_METHOD"] == "POST"){
        if ($_POST["searchWord"]) $propertyArray["search.word.list.str"] = $_POST["searchWord"];
        if ($_POST["tweetFetchCount"]) $propertyArray["tweet.fetch.count"] = $_POST["tweetFetchCount"];
        if ($_POST["isFetchRetweet"]) $propertyArray["is.fetch.retweet"] = $_POST["isFetchRetweet"];
        if ($_POST["consumeKey"]) $propertyArray["consumer.key"] = $_POST["consumeKey"];
        if ($_POST["consumeSecret"]) $propertyArray["consumer.secret"] = $_POST["consumeSecret"];
        if ($_POST["accessToken"]) $propertyArray["access.token"] = $_POST["accessToken"];
        if ($_POST["accessTokenSecret"]) $propertyArray["access.token.secret"] = $_POST["accessTokenSecret"];
        
        try {
            $fp = fopen($filename, 'w');
            foreach ($propertyArray as $k => $i) fputs($fp, "$k=$i\n");
            fclose($fp);
            print '<script type="text/javascript">editComplete();</script>';
        } catch (Exception $ex) {
            echo TEST;
        }
    }
    
?>
<form method="post" action="./edit.php?facetName=<?php echo $_GET[facetName]?>">
    <div class="form-group">
        <label><u>検索するワードを指定します。(複数指定する場合は、【,】で区切ってください)</u></label><br>
            ・特定のユーザのツィートを指定したい場合は、【@ユーザ名】のように先頭に@を付与してください。<br>
            ・特定のハッシュタグのツィートを検索したい場合は、【#ハッシュタグ】のように先頭に#を付与してください。<br>
            例：ユーザToyTalkとハッシュタグToyTalkとToyTalkを含むツイートを検索したい場合、【@ToyTalk,#ToyTalk,ToyTalk】<br>
        <input class="form-control" type="text" name="searchWord" value="<?php print $propertyArray["search.word.list.str"] ?>">
    </div>
    <div class="form-group">
        <label><u>最新何件を取得して読み上げるかを指定します。</u></label><br>
        ※ 複数検索ワードが存在する場合は、全てのワードを対象に検索して最新投稿から指定された件数分読み上げます。<br>
        <select name="tweetFetchCount">
            <option value="1" <?php if ($propertyArray["tweet.fetch.count"] === "1") echo selected; ?>> 1件</option>
            <option value="2" <?php if ($propertyArray["tweet.fetch.count"] === "2") echo selected; ?>> 2件</option>
            <option value="3" <?php if ($propertyArray["tweet.fetch.count"] === "3") echo selected; ?>> 3件</option>
            <option value="4" <?php if ($propertyArray["tweet.fetch.count"] === "4") echo selected; ?>> 4件</option>
            <option value="5" <?php if ($propertyArray["tweet.fetch.count"] === "5") echo selected; ?>> 5件</option>
            <option value="6" <?php if ($propertyArray["tweet.fetch.count"] === "6") echo selected; ?>> 6件</option>
            <option value="7" <?php if ($propertyArray["tweet.fetch.count"] === "7") echo selected; ?>> 7件</option>
            <option value="8" <?php if ($propertyArray["tweet.fetch.count"] === "8") echo selected; ?>> 8件</option>
            <option value="9" <?php if ($propertyArray["tweet.fetch.count"] === "9") echo selected; ?>> 9件</option>
            <option value="10" <?php if ($propertyArray["tweet.fetch.count"] === "10") echo selected; ?>> 10件</option>
        </select>
    </div>
    <div class="form-group">
        <label><u>リツィートを読み上げ対象とするかを指定します。</u></label><br>
        <select name="isFetchRetweet">
            <option value="true" <?php if ($propertyArray["is.fetch.retweet"] == 1 | $propertyArray["is.fetch.retweet"] == true) echo selected; ?>> 読み上げ対象とする</option>
            <option value="false" <?php if ($propertyArray["is.fetch.retweet"] == 0 | $propertyArray["is.fetch.retweet"] == false) echo selected; ?>> 読み上げ対象としない</option>
        </select>
    </div>
    <div class="form-group">
        <label><u>TwitterAPIKeyのConsumeKeyを指定して下さい。</u></label><br>
        <input class="form-control" type="text" name="consumeKey" value="<?php print $propertyArray["consumer.key"] ?>">
    </div>
    <div class="form-group">
        <label><u>TwitterAPIKeyのConsumeSecretを指定して下さい。</u></label><br>
        <input class="form-control" type="text" name="consumeSecret" value="<?php print $propertyArray["consumer.secret"] ?>">
    </div>
    <div class="form-group">
        <label><u>TwitterAPIKeyのAccessTokenを指定して下さい。</u></label><br>
        <input class="form-control" type="text" name="accessToken" value="<?php print $propertyArray["access.token"] ?>">
    </div>
    <div class="form-group">
        <label><u>TwitterAPIKeyのAccessTokenSecretを指定して下さい。</u></label><br>
        <input class="form-control" type="text" name="accessTokenSecret" value="<?php print $propertyArray["access.token.secret"] ?>">
    </div>
    
    <input class="btn btn-primary" type="submit" value="変更">
</form>
