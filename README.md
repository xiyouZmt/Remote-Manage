## 基于Socket的远程操作助手

* 1 客户端和pc端必须在同一个局域网下；
* 2 在连接时pc端需要关闭防火墙，不过我试过有时候不关也可以，但是最好还是关闭；
* 3 在连接完成后Socket和流都要close掉，否则数据传不过去；

到这客户端连接pc的工作基本上就完成了。接下来就是服务端的任务了。启动服务端，在接收到客户端的请求之后连接到客户端，这下就能够传文件到pc了，在做这个项目的时候我在传文件的基础上还附加了一些传输命令功能，可以在客户端直接控制pc执行一些操作，像常用的电源，音量，亮度，鼠标控制等等，方便了对pc的控制。

其它的操作都是一些简单的命令执行，下面附上一些常用命令：

* “Shutdown -s” 关机
* “Shutdown -h” 睡眠
* “Shutdown -r” 重启
* “explorer” 资源管理器
* “cmd –c start teskmgr” 任务管理器
* "cmd /c start" 命令提示符
* “cmd /c start control” 控制面板
* “notepad” 记事本
* “calc” 计算器
* “dvdplay Windows” Windows MediaPlay
* “write” 写字板
* “mspaint” 画图板
* “cmd /c start start www.baidu.com”访问百度
* “cmd /c start start www.baidu.com/s?wd=xxxxx" 搜索xxxxx

到这里，Socket实现文件传输就全部介绍完了。通过这个项目，我对Socket的理解更加深入，了，这对以后的学习也会有很大帮助，希望这些内容能对大家有所帮助。
