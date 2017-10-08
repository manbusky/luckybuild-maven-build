
# 打包的时候设置模板文件中引用的 JS 文件发布序号

## 问题起源

更新 WEB 程序的时候，经常伴随着 `javascript` 文件的更新；由于浏览器的一些缓存策略，用户访问新发布的 WEB 程序的时候经常无法访问到最新`javascript`文件，需要用户手动清理浏览器缓存；

使用旧的（浏览器缓存的）`javascript`文件，严重的时候会影响到程序的正确运行，甚至操作出来脏数据，特别是使用`javascript`文件会修改DOM结构的时候；

## 解决方法

一般要求要修改了`javascript`文件的时候，对应修改模板文件引用的路径，加上一个引用版本号，例如：

修改了`main.js`文件，则修改引的地方；


```
 <script type="text/javascript" src="main.js?v=1001"></script>

```

这样，修改了引用路径，浏览器会认为是一个新的资源，会重新从服务器请求最新的文件内容；


## 使用此插件的解决办法

此插件是工作在 `Maven` 的`package`之前，将模板引用静态文件的路径的地方增加发布序号，实际上是打包时的时间格式`yyMMddHHmm`，例如：


```
 <script type="text/javascript" src="main.js?v=1710061648"></script>

```

## Get start

### `pom.xml` 增加配置

`pom.xml` 文件的`build`->`plugins`节点下增加插件配置，如下：


```
<plugin>
    <groupId>manbu.maven.plugin</groupId>
    <artifactId>luckybuild-maven-build</artifactId>
    <version>0.9.0</version>
    <executions>
        <execution>
            <phase>prepare-package</phase>
            <goals>
                <goal>sequence</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <encoding>UTF-8</encoding>
        <skips>
            <skip>jquery-2.1.4.min.js</skip>
            <skip>bootstrap.min.js</skip>
        </skips>
        <includes>
            <inlcude>**/*.jsp</inlcude>
            <inlcude>**/*.html</inlcude>
        </includes>
    </configuration>
    </plugin>

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-war-plugin</artifactId>
        <configuration>
        <warSourceExcludes>**/*.jsp,**/*.html</warSourceExcludes>
    </configuration>
</plugin>

```

插件`luckybuild-maven-plugin`，会将`includes`的节点配置的文件中`javascript`文件路径后面增加序号。

- 通过`includes`节点配置需要处理的模板文件；

- 通过 `skips` 配置一些不需要加发布序号的，例如`jQuery`、`Bootstrap`等，以助于浏览器使用缓存中的文件，增加页面展示效率；

配置插件`maven-war-plugin`，是将`luckybuild-maven-plugin`插件中处理的模板文件进行剔除，不然加完序号后的模板文件会被此插件在资源拷贝时覆盖掉了，这个配置很重要！

运行 `mvn clean package` 看看吧~~

### Have fun~


