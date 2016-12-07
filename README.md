# VariousBle
一个支持连接多种类型和多个设备的低功耗蓝牙通信模块。模块和界面充分解耦并且易于扩展。
# 写这个模块的原因
最近在开发一个移动健康应用，需要同时支持多种智能可穿戴设备。每个设备的UUID和消息处理都不一样，同时模块还需要与界面交互。按照常规的写法，需要在service中依赖和定义各种各样的callback和方法，并且需要强行记住它们之间的配对关系，比如connect1()只能调用callback1,connect2()只能调用callback2等，这样一来service的代码非常庞杂，并且难以维护和扩展。
# 模块的理念
1、界面和service之间的通信不通过回调来实现，因为这样会造成他们之间的相互依赖。而是使用EventBus消息总线来进行通信，虽然效率会稍微下降，但是这点代价是值得的。

2、界面通过自定义Event与service通信，可以将Event全部集中到一个模块。那么只有界面依赖Event，Service依赖了Event。界面就跟service完全解耦了。这里要注意界面不一定是指启动service的app模块。例如大的app可以将界面分成血压界面、运动界面等几个模块，那么这些模块中的Activity等虽然需要与service通信，但是可以不依赖于service模块。另外其他模块如数据缓存和处理模块也只需要依赖Event不需要依赖service。

3、各种蓝牙设备的不同之处体现在BluetoothGattCallback中对相应消息的处理不同，同时也有一些共有的处理，如蓝牙连接成功和断开，它们都需要读、写和服务的UUID等，都只处理自己自定义的Event等，那么将这些共性抽取出来作为一个抽象的GattCallback类，由其实现类去操作细节，Service就不用频繁改动了。

4、基于EventBus里Event可以继承的强大特性，Service只接收界面事件的基类SrvEvent，并自动处理一些通用的事件，如开始停止扫描、连接或断开某个蓝牙等。遇到不能处理的自定义事件再转交给 事件对应该的 GattCallback去处理。

5、Service维护一个HashMap，键为设备的address，值为GattMgr的不同设备实现类的实例。GattMgr实现了BluetoothGattCallback并且管理着一个BluetoothGatt。当Service收到一个连接请求时，先通过address查找HashMap中有没有对应的GattMgr。如果没有，则需要通过SrvCfg中配置的规则找出其对应的GattMgr。这个规则指的是通过设备名称或者地址列表等找出它是哪种设备，如血压计的名称一般都是BPM-开头。之后处理、回应等都交给了GattMgr实现。

# 模块的现状
基本框架已经实现，如果增加新的设备，不需要改动Service中的代码，只需在Event模块中定义好事件，实现其GattMgr类，并在SrvCfg类中配置好事件、设备类型和GattMgr实现类的关联规则即可。
