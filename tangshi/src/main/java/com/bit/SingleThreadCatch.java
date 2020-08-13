package com.bit;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import java.io.IOException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class SingleThreadCatch {
    public static void main(String[] args) throws Exception {
        WebClient webClient=new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        String baseUrl="https://so.gushiwen.org";
        String pathUrl="/gushi/tangshi.aspx";

        List<String> detailUrlList=new ArrayList<>();
        // 列表页下的解析
        {
            String url = baseUrl + pathUrl;
            HtmlPage page = webClient.getPage(url);
            List<HtmlElement> elements = page.getBody().getElementsByAttribute(
                    "div",
                    "class",
                    "typecont"
            );
            for (HtmlElement element : elements) {
                List<HtmlElement> as = element.getElementsByTagName("a");
                for (HtmlElement a : as) {
                    String detailUrl = a.getAttribute("href");
                    detailUrlList.add(baseUrl + detailUrl);
                }
            }
        }

        MysqlConnectionPoolDataSource dataSource=new MysqlConnectionPoolDataSource();
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3306);
        dataSource.setUser("root");
        dataSource.setPassword("123456");
        dataSource.setDatabaseName("tangshi");
        dataSource.setUseSSL(true);
        dataSource.setCharacterEncoding("utf8");

        Connection connection=dataSource.getConnection();
        String sql = "INSERT INTO t_tangshi " +
                "(sha256, dynasty, title, author, content, words) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement=connection.prepareStatement(sql);

        // 详情页的请求和解析
        for(String url:detailUrlList){
            HtmlPage page=webClient.getPage(url);
            String xpath;
            DomText domText;

            // 标题
            xpath="//div[@class='cont']/h1/text()";
            domText=(DomText)page.getBody().getByXPath(xpath).get(0);
            String title=domText.asText();
            // 朝代
            xpath="//div[@class='cont']/p[@class='source']/a[1]/text()";
            domText=(DomText)page.getBody().getByXPath(xpath).get(0);
            String dynasty=domText.asText();
            // 作者
            xpath="//div[@class='cont']/p[@class='source']/a[2]/text()";
            domText=(DomText)page.getBody().getByXPath(xpath).get(0);
            String author=domText.asText();
            // 正文
            xpath="//div[@class='cont']/div[@class='contson']";
            HtmlElement element=(HtmlElement)page.getBody().getByXPath(xpath).get(0);
            String content=element.getTextContent().trim();

            // 1. 计算 sha-256
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            String s=title+content;
            messageDigest.update(s.getBytes("UTF-8"));
            byte[] result=messageDigest.digest();
            StringBuilder sha256=new StringBuilder();
            for(byte b:result){
                sha256.append(String.format("%02x",b));
            }

            // 2. 计算分词
            List<Term> termList=new ArrayList<>();
            termList.addAll(NlpAnalysis.parse(title).getTerms());
            termList.addAll(NlpAnalysis.parse(content).getTerms());
            List<String> words=new ArrayList<>();
            for(Term term:termList){
                if(term.getNatureStr().equals("w")){
                    continue;
                }
                if(term.getNatureStr().equals("null")){
                    continue;
                }
                if(term.getRealName().length()<2){
                    continue;
                }
                words.add(term.getRealName());
            }
            String insertWords=String.join(",",words);

            statement.setString(1,sha256.toString());
            statement.setString(2,dynasty);
            statement.setString(3,title);
            statement.setString(4,author);
            statement.setString(5,content);
            statement.setString(6,insertWords);

            com.mysql.jdbc.PreparedStatement mysqlStatement= (com.mysql.jdbc.PreparedStatement) statement;
            System.out.println(mysqlStatement.asSql());
            statement.executeUpdate();
            System.out.println("插入成功");
        }
    }
}
