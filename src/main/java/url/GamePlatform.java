package url;

import java.io.*;
import java.net.*;
import java.util.regex.*;

import main.GameInfo;

import static main.GameInfo.Element.*;

public enum GamePlatform {
    STEAM(
        "Steam",
        "<p class=\"game_purchase_discount_countdown\">",
        "https://store.steampowered.com/app/<prodID>/",
        "?cc=jp&l=japanese",
        Pattern.compile("https{0,1}://store\\.steampowered\\.com/app/"),
        Pattern.compile("https{0,1}://store\\.steampowered\\.com/app/(\\d*)/{0,1}.*"),
        Pattern.compile("<span itemprop=\"name\">([^<]*)</span>"),
        Pattern.compile("<img class=\"game_header_image_full\" src=\"([^?]*)[^>]*>"),
        Pattern.compile("<div class=\"game_purchase_price price\"[^>]*>([^<]*)</div>"),
        Pattern.compile("<div class=\"discount_original_price\">([^<]*)</div>"),
        Pattern.compile("<div class=\"discount_final_price\">([^<]*)</div>"),
        Pattern.compile("<div class=\"discount_pct\">([^<]*)</div>")
    ),
    UBISOFT(
        "Ubisoft",
        null,
        "https://store.ubi.com/jp/<prodID>.html",
        null,
        Pattern.compile("https{0,1}://store\\.ubi\\.com/jp/"),
        Pattern.compile("https{0,1}://store\\.ubi\\.com/jp/(game\\?pid=|[^/]*/)([^\\.|&]*)"),
        Pattern.compile("\"gameName\":\"([^\"]*)\","),
        Pattern.compile("\"image_url\":\"([^\"]*)"),
        null,
        Pattern.compile("\"unit_price\":([\\d]*)"),
        Pattern.compile("\"unit_sale_price\":([\\d]*)"),
        null
    ),
    NINTENDO(
        "Nintendo",
        "<div class=\"productDetail--detail__price--onSale\">",
        "https://store-jp.nintendo.com/list/software/<prodID>.html",
        null,
        Pattern.compile("https{0,1}://store-jp\\.nintendo\\.com/list/software/"),
        Pattern.compile("https{0,1}://store-jp\\.nintendo\\.com/list/software/(\\d*)\\.html"),
        Pattern.compile("<h1 class=\"productDetail--headline__title\">([^<]*)</h1>"),
        Pattern.compile("<meta property=\"og:image\" content=\"([^?]*)[^>]*>"),
        Pattern.compile("<div class=\"productDetail--detail__price js-productMainRenderedPrice\"><span>([^<]*)</span>"),
        Pattern.compile("<div class=\"productDetail--detail__price js-productMainRenderedPrice\"><span>([^<]*)</span>"),
        Pattern.compile("<div class=\"productDetail--detail__priceDeleted\"><span>([^<]*)</span>"),
        Pattern.compile("<em class=\"productDetail--detail__priceDiscount\">([^<]*)</em>")
    ),
    PLAYSTATION(
        "PlayStation",
        "\"discountText\":\"-",
        "https://store.playstation.com/ja-jp/<prodID>",
        null,
        Pattern.compile("https{0,1}://store\\.playstation\\.com/ja-jp/"),
        Pattern.compile("https{0,1}://store\\.playstation\\.com/ja-jp/(.*)"),
        Pattern.compile("\"__typename\":\"Concept\",\"name\":\"([^\"]*)\","),
        Pattern.compile("\"EDITION_KEY_ART\",\"url\":\"([^\"]*)\""),
        Pattern.compile("\"originalPriceValue\":([^,]*),"),
        Pattern.compile("\"originalPriceValue\":([^,]*),"),
        Pattern.compile("\"discountPriceValue\":([^,]*),"),
        Pattern.compile("\"discountText\":\"([^,]*)\",")
    ),
    MICROSOFT(
        "Microsoft",
        "\"IsOnSale\":true",
        "https://www.microsoft.com/ja-jp/p/<prodName>/<prodID>",
        "?activetab=pivot:overviewtab",
        Pattern.compile("https{0,1}://www\\.microsoft\\.com/ja-jp/p/"),
        Pattern.compile("https{0,1}://www\\.microsoft\\.com/ja-jp/p/([^/]*)/([^\\?]*)"),
        Pattern.compile("\"pageName\":\"([^\"]*)\","),
        Pattern.compile("<picture id=\"dynamicImage_backgroundImage_picture\" aria-disabled=\"false\" class=\"c-image\" data-reactroot=\"\"><source srcSet=\"([^\\?]*)[^>]*>"),
        Pattern.compile("\"rtPrice\":([^,]*),"),
        Pattern.compile("\"rtPrice\":([^,]*),"),
        Pattern.compile("\"lstPrice\":([^,]*),"),
        Pattern.compile("\"DiscountPercent\":\"([^\"]*)\",")
    );

    private final String name,
        dealJudge,
        urlStructs,
        reqParam;

    private final Pattern urlPattern,
        idPattern,
        titlePattern,
        imageUrlPattern,
        normalPricePattern,
        basePricePattern,
        dealPricePattern,
        discountAmountPattern;

    private GamePlatform(
        String name,
        String dealJudge,
        String urlStructs,
        String reqParam,
        Pattern urlPattern,
        Pattern idPattern,
        Pattern titlePattern,
        Pattern imageUrlPattern,
        Pattern normalPricePattern,
        Pattern basePricePattern,
        Pattern dealPricePattern,
        Pattern discountAmountPattern
    ) {
        this.name = name;
        this.dealJudge = dealJudge;
        this.urlStructs = urlStructs;
        this.reqParam = reqParam;
        this.urlPattern = urlPattern;
        this.idPattern = idPattern;
        this.titlePattern = titlePattern;
        this.imageUrlPattern = imageUrlPattern;
        this.normalPricePattern = normalPricePattern;
        this.basePricePattern = basePricePattern;
        this.dealPricePattern = dealPricePattern;
        this.discountAmountPattern = discountAmountPattern;
    }

    public static GamePlatform getPlatformByUrl(String url) {
        for (GamePlatform gp : GamePlatform.values()) {
            Matcher m = gp.urlPattern.matcher(url);
            if (m.find()) return gp;
        }

        return null;
    }

    public static GamePlatform getPlatformByName(String name) {
        for (GamePlatform gp : GamePlatform.values()) {
            if (gp.name.toLowerCase().equals(name.toLowerCase())) return gp;
        }

        return null;
    }

    public String getGameTitle(String html) {
        Matcher m = titlePattern.matcher(html);
        if (m.find()) return m.group(1).trim();
        return null;
    }

    public void getPriceInfo(GameInfo gi, String prodID) throws Exception {
        String html = getHtml(getSimpleUrl(prodID));
        Matcher m;
        String basePrice = "N/A", lastPrice = "N/A", discount = "N/A";
        boolean isDeal;
        boolean isCalcDisc = false;

        if (this == UBISOFT) {
            isDeal = true;
            isCalcDisc = true;

            Pattern p = Pattern.compile("(window.universal_variable.product[^;]*)");
            m = p.matcher(html);
            if (m.find()) html = m.group(1);
        }
        else isDeal = html.contains(dealJudge);

        if (isDeal) {
            m = basePricePattern.matcher(html);
            if (m.find()) basePrice = m.group(1).trim();
            m = dealPricePattern.matcher(html);
            if (m.find()) lastPrice = m.group(1).trim();

            if (!isCalcDisc) {
                if (!basePrice.equals(lastPrice)) {
                    m = discountAmountPattern.matcher(html);
                    if (m.find()) discount = m.group(1).trim();
                } else {
                    discount = "0";
                }
            }
        } else {
            m = normalPricePattern.matcher(html);

            if (m.find()) {
                String price = m.group(1).trim();
                basePrice = price;
                lastPrice = price;
                discount = "0";
            }
        }

        int baseNum;
        int lastNum;
        double discountNum;

        try {
            baseNum = (int) Double.parseDouble(
                basePrice.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            baseNum = 0;
        }

        try {
            lastNum = (int) Double.parseDouble(
                lastPrice.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            lastNum = 0;
        }

        if (isCalcDisc) discountNum = 100.0 * (1.0 - (double) lastNum / baseNum);
        else discountNum = Integer.parseInt(discount.replaceAll("\\D", ""));

        gi.setValue(BASE_PRICE, baseNum);
        gi.setValue(LAST_PRICE, lastNum);
        gi.setValue(DISCOUNT, (int) discountNum);
    }

    public String getProdID(String url) {
        Matcher m = idPattern.matcher(url);
        if (!m.find()) return null;
        String prodID = m.group(m.groupCount());
        if (this.equals(MICROSOFT)) return prodID + "," + m.group(m.groupCount() - 1);
        else return prodID;
    }

    public String getSimpleUrl(String prodID) {
        String url;
        if (reqParam == null) url = urlStructs;
        else url = urlStructs + reqParam;

        if (this.equals(MICROSOFT)) {
            String[] strings = prodID.split(",");
            return url
                .replaceAll("<prodID>", strings[0])
                .replaceAll("<prodName>", strings[1]);
        }
        else return url.replaceAll("<prodID>", prodID);
    }

    public String getImageUrl(String html) {
        Matcher m;

        if (this == UBISOFT) {
            Pattern p = Pattern.compile("(window.universal_variable.product[^;]*)");
            m = p.matcher(html);
            if (m.find())
                html = m.group(1);
        }

        m = imageUrlPattern.matcher(html);
        if (!m.find())
            return null;
        String imageUrl = m.group(m.groupCount());
        return imageUrl;
    }

    public String getHtml(String url) {
        BufferedReader br;

        try {
            URLConnection con = new URL(url).openConnection();
            br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                sb.append(line.trim());
            }

            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String getName() {return name;}
}
