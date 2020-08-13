import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HtmlUnitDemo {
    public static void main(String[] args) throws IOException {
        // 无界面的浏览器（HTTP 客户端）
        // 用 WebClient 创建一个浏览器
        // BrowserVersion.CHROME 模拟的是 Chrome 版本的浏览器
        try (WebClient webClient = new WebClient(BrowserVersion.CHROME)) {

            // 关闭了浏览器的 js 执行引擎，不在执行网页中的 js 脚本
            webClient.getOptions().setJavaScriptEnabled(false);
            // 关闭了浏览器的 css 执行引擎，不在执行网页中的 css 布局
            webClient.getOptions().setCssEnabled(false);

            // webClient.getPage(...) 可以对页面进行请求了
            // 抛出 IO 异常
            // HtmlPage 实例不应直接实例化
            // webClient.getPage(String) 当服务器响应的内容类型为 text/html 时，将返回他们
            String url="https://so.gushiwen.org/gushi/tangshi.aspx";
            HtmlPage page = webClient.getPage(url);
            // System.out.println(page);
            // 通过抓包验证一下结果
            // 通过“域名”得到 IP 地址
            // ping so.gushiwen.org (只能得到其中一个 IP 地址)
            // nslookup so.gushiwen.org (能得到所有 IP 地址)

            // page.save(new File("唐诗三百首\\列表页.html"));
            // save 只能存一遍
            // File file = new File("唐诗三百首\\列表页.html");
            // file.delete();
            // page.save(file);

            // 如何从 html 中提取我们需要的信息
            // getBody()   返回 body 元素，或者 null 尚不存在
            HtmlElement body = page.getBody();
            List<HtmlElement> elements = body.getElementsByAttribute(
                    "div",          // 元素名称
                    "class",       // 属性名称
                    "typecont"     // 属性值
            );

            int count=0;
            for (HtmlElement element : elements) {
                // System.out.println(element);

                // 获取 a 标签
                List<HtmlElement> as=element.getElementsByTagName("a");
                for(HtmlElement a:as){
                    // 打印路径 (/shiwenv_703276b8042c.aspx)
                    System.out.println(a.getAttribute("href"));
                    count++;
                }
            }
            System.out.println(count);

            /*
            // 取第一个(只获取了唐诗三百首)
            HtmlElement divElement = elements.get(0);
            // 找出所有的 a 标签
            List<HtmlElement> aElements = divElement.getElementsByAttribute(
                    "a",
                    "target",
                    "_blank"
            );
            for (HtmlElement e : aElements) {
                System.out.println(e);
            }

            System.out.println(aElements.size());
            System.out.println(aElements.get(0).getAttribute("href"));
            */
        }
    }
}
