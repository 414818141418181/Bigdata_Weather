# 项目背景：
* 适用于大数据专业学生进行初级练手的一个大数据项目，主要内容是调用api接口，调用数据，通过数据清洗，数据治理以及数据可视化来进行项目的实现。
## 调用接口：
* 调用api接口来进行获取数据源
```
def get_data(city_code):
    urls = f"http://t.weather.sojson.com/api/weather/city/{city_code}"
    response = requests.get(urls)
    if response.status_code == 200:
        forecast_data = response.json()
        weather_data = forecast_data["data"]["forecast"]
        print(weather_data)
    else:
        print("无法获取天气预报数据，状态码:", response.status_code)

    return weather_data
```
* 将初级得到的数据上传至hadoop集群
```
,date,high,low,ymd,week,sunrise,sunset,aqi,fx,fl,type,notice
0,19,高温 35℃,低温 24℃,2023-08-19,星期六,05:30,19:06,87,南风,1级,晴,愿你拥有比阳光明媚的心情
1,20,高温 32℃,低温 24℃,2023-08-20,星期日,05:31,19:04,61,南风,2级,多云,阴晴之间，谨防紫外线侵扰
2,21,高温 31℃,低温 22℃,2023-08-21,星期一,05:32,19:03,83,南风,2级,多云,阴晴之间，谨防紫外线侵扰
3,22,高温 32℃,低温 23℃,2023-08-22,星期二,05:33,19:02,83,北风,2级,晴,愿你拥有比阳光明媚的心情
4,23,高温 31℃,低温 21℃,2023-08-23,星期三,05:34,19:00,87,东风,2级,晴,愿你拥有比阳光明媚的心情
5,24,高温 28℃,低温 21℃,2023-08-24,星期四,05:35,18:59,67,南风,2级,阴,不要被阴云遮挡住好心情
6,25,高温 30℃,低温 21℃,2023-08-25,星期五,05:36,18:57,82,南风,2级,多云,阴晴之间，谨防紫外线侵扰
7,26,高温 22℃,低温 16℃,2023-08-26,星期六,05:37,18:56,56,东风,2级,大雨,出门最好穿雨衣，勿挡视线
8,27,高温 24℃,低温 15℃,2023-08-27,星期日,05:37,18:54,45,北风,1级,多云,阴晴之间，谨防紫外线侵扰
9,28,高温 28℃,低温 17℃,2023-08-28,星期一,05:38,18:53,39,东北风,2级,阴,不要被阴云遮挡住好心情
10,29,高温 29℃,低温 19℃,2023-08-29,星期二,05:39,18:51,40,东北风,1级,阴,不要被阴云遮挡住好心情
11,30,高温 28℃,低温 19℃,2023-08-30,星期三,05:40,18:50,69,西北风,2级,小雨,雨虽小，注意保暖别感冒
12,31,高温 30℃,低温 19℃,2023-08-31,星期四,05:41,18:48,50,西风,2级,晴,愿你拥有比阳光明媚的心情
13,01,高温 30℃,低温 20℃,2023-09-01,星期五,05:42,18:47,62,北风,1级,晴,愿你拥有比阳光明媚的心情
14,02,高温 31℃,低温 20℃,2023-09-02,星期六,05:43,18:45,47,西南风,2级,晴,愿你拥有比阳光明媚的心情
```
### 简单的案例
#### 数据处理:
* 使用mapreduce来对数据做一个初级的处理，以日期为key值，取出日落与日出时间，为后期数据可视化做准备
* 在使用MapReduce时需要导入对应的依赖
```
 <dependencies>
        <!--hadoop-client依赖-->
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>3.2.1</version>
            <!--表示只在编译的时候使用这个依赖,在执行以及打包的时候都不使用-->
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.60</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.cetc.reimbursement.View.reimture</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.0.2</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                </configuration>
            </plugin>
        </plugins>

    </build>
```
* sun是自定义的一个实体类，用于存放多个数值
```
 public static class WMap extends Mapper<LongWritable, Text, Text,Sun>{
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text,Text, Sun>.Context context) throws IOException, InterruptedException {
            String lane = value.toString();
            String[] data = lane.split(",");
            if(key.toString().equals("0")) {

            }else{
                String ymd = data[4];
                String sunrise =data[6];
                String sunset = data[7];
                Text k2 =new Text(ymd);
                Sun v2 = new Sun(sunrise, sunset);
                context.write(k2, v2);
            }
        }
    }
public class Sun implements Writable {
    String sunrise;
    String sunset;

    public Sun() {
        // 无参构造函数
    }

    public Sun(String sunrise, String sunset) {
        this.sunrise =sunrise;
        this.sunset = sunset;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(sunrise);
        dataOutput.writeUTF(sunset);
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        sunrise = dataInput.readUTF();
        sunset = dataInput.readUTF();
    }
    public String toString() {
        return sunrise+ "\t" +sunset;
    }
2023-08-19      06:04   18:56
2023-08-20      06:05   18:55
2023-08-21      06:05   18:54
2023-08-22      06:05   18:54
2023-08-23      06:06   18:53
2023-08-24      06:06   18:52
2023-08-25      06:07   18:51
2023-08-26      06:07   18:50
2023-08-27      06:07   18:49
2023-08-28      06:08   18:48
2023-08-29      06:08   18:47
2023-08-30      06:08   18:46
2023-08-31      06:08   18:46
2023-09-01      06:09   18:45
2023-09-02      06:09   18:44
```
* sun类就不再演示，在MapReduce里可以找到源文件
* 使用sqoop写入本地数据库,先创建数据库以及表
```
  CREATE DATABASE weather DEFAULT CHARACTER SET=utf8 DEFAULT COLLATE
=utf8_general_ci;
USE weather;
CREATE TABLE beij (
 date varchar(255),
 sunrise varchar(255),
 sunset varchar(255)
);
  sqoop export --connect jdbc:mysql://本机的ip地址:3306/weather?serverTimezone=UTC \
  --username hostname \
  --password password \
  --table beij  \
  --export-dir /weather/beij/ \
  --input-fields-terminated-by "\t"
```
#### 数据可视化:
```
def data_show(hostname,username,password,database,table_name):
# 连接数据库
    connection = pymysql.connect(host=hostname, user=username, password=password, database=database)

# 查询数据
    query = f"SELECT date,sunrise,sunset FROM {table_name} ORDER BY date,sunrise,sunset ASC"
    cursor = connection.cursor()
    cursor.execute(query)

# 获取结果集
    results = cursor.fetchall()

# 准备数据
    data_sunrise = []
    data_sunset = []
    xlab = []
    for row in results:
        xlab.append(row[0])
        data_sunrise.append(row[1])
        data_sunset.append(row[2])

# 绘制图表
    fig = plt.figure(figsize=(15, 8))
    plt.plot(xlab,data_sunset)
    plt.plot(xlab,data_sunrise)
    plt.title('日初日落时间')
    plt.show()

# 关闭连接
    cursor.close()
    connection.close()
```
![image](https://github.com/414818141418181/Bigdata_Weather/assets/128785226/6e725c9c-4368-442b-b8a9-11761cb2dbed)
### 进阶案例
* 使用mapreduce取出温度并计算当天的温差，同样tem是自定义类
```
public class temp {
    public static class TMap extends Mapper<LongWritable, Text, Text, tem> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] data = line.split(",");
            String high_s = data[2];
            String low_s = data[3];
            String ymd = data[4];

            Pattern pattern = Pattern.compile("\\d+"); // 匹配数字的正则表达式
            Matcher matcher_h = pattern.matcher(high_s);
            Matcher matcher_l = pattern.matcher(low_s);

            String high1 = "";
            String low1 = "";

            if (matcher_h.find()) {
                high1 = matcher_h.group();
            }

            if (matcher_l.find()) {
                low1 = matcher_l.group();
            }

            if (!high1.isEmpty() && !low1.isEmpty()) { // 只处理找到匹配数字的情况
                LongWritable high = new LongWritable(Long.parseLong(high1));
                LongWritable low = new LongWritable(Long.parseLong(low1));

                LongWritable differ = new LongWritable(high.get() - low.get());//计算温差
                Text k2 = new Text(ymd);
                tem v2 = new tem(high, low, differ);
                context.write(k2, v2);
            }
        }
    }


    public static class TReduce extends Reducer<Text, tem, Text, tem> {
        @Override
        protected void reduce(Text key, Iterable<tem> values, Reducer<Text, tem, Text, tem>.Context context) throws IOException, InterruptedException {
            for (tem v2 : values) {
                String high = v2.getHigh().toString() + " \u2103";#给每一个数值加上摄氏度的标签
                String low = v2.getLow().toString() + " \u2103";
                String differ = v2.getDiffer().toString() + " \u2103";
                context.write(key, new tem(high, low, differ));
            }
        }
    }
public class tem implements Writable {
    private LongWritable high;
    private LongWritable low;
    private LongWritable differ;
    private String high_t;
    private String low_t;
    private String differ_t;

    public tem() {

    }

    public tem(LongWritable high, LongWritable low, LongWritable differ) {
        this.high = high;
        this.low = low;
        this.differ = differ;
        this.high_t = "";
        this.low_t = "";
        this.differ_t = "";
    }

    public tem(String high_t, String low_t, String differ_t) {
        this.high_t = high_t;
        this.low_t = low_t;
        this.differ_t = differ_t;
    }

    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(high.get());
        dataOutput.writeLong(low.get());
        dataOutput.writeLong(differ.get());
        dataOutput.writeUTF(high_t);
        dataOutput.writeUTF(low_t);
        dataOutput.writeUTF(differ_t);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.high = new LongWritable();
        this.high.readFields(dataInput);
        this.low = new LongWritable();
        this.low.readFields(dataInput);
        this.differ = new LongWritable();
        this.differ.readFields(dataInput);
        this.high_t = dataInput.readUTF();
        this.low_t = dataInput.readUTF();
        this.differ_t = dataInput.readUTF();
    }

    public String toString() {
        return high_t+ "\t" +low_t+ "\t" +differ_t;
    }

```
* 以下是处理好的数据
```
2023-08-19      35 ℃    24 ℃    11 ℃
2023-08-20      32 ℃    24 ℃    8 ℃
2023-08-21      31 ℃    22 ℃    9 ℃
2023-08-22      32 ℃    23 ℃    9 ℃
2023-08-23      31 ℃    21 ℃    10 ℃
2023-08-24      28 ℃    21 ℃    7 ℃
2023-08-25      30 ℃    21 ℃    9 ℃
2023-08-26      22 ℃    16 ℃    6 ℃
2023-08-27      24 ℃    15 ℃    9 ℃
2023-08-28      28 ℃    17 ℃    11 ℃
2023-08-29      29 ℃    19 ℃    10 ℃
2023-08-30      28 ℃    19 ℃    9 ℃
2023-08-31      30 ℃    19 ℃    11 ℃
2023-09-01      30 ℃    20 ℃    10 ℃
2023-09-02      31 ℃    20 ℃    11 ℃
```
* 使用sqoop工具将处理好的数据上传至本地数据库
```
USE weather;
CREATE TABLE tem (
 date varchar(255),
 high varchar(255),
 low varchar(255),
 differ varchar(255)
);
 
  sqoop export --connect jdbc:mysql://本机ip:3306/weather?serverTimezone=UTC \
  --username hostname \
  --password password \
  --table tem  \
  --export-dir /weather/temp/ \
  --input-fields-terminated-by "\t" 
```
* 数据可视化,这里便不再演示可视化
## Hive数据分析
### 数据预处理
* 先将weather.csv表格清洗，去除不必要的值和列名
```
   public static class HMap extends Mapper<LongWritable, Text,Text,data>{
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, data>.Context context) throws IOException, InterruptedException {
            String lane = value.toString();
            String[] data1 = lane.split(",");
            if(!key.toString().equals("0")) {
                String high = data1[2];
                String low = data1[3];
                String ymd = data1[4];
                String week = data1[5];
                String sunrise = data1[6];
                String sunset = data1[7];
                LongWritable aqi = new LongWritable(Long.parseLong(data1[8]));
                String fx = data1[9];
                String fl = data1[10];
                String type = data1[11];
                String notice = data1[12];
                Text k2 = new Text(ymd);
                data v2 = new data(high, low, week, sunrise, sunset, aqi, fx, fl, type, notice);
                context.write(k2, v2);
            }
        }
   public data(String high,String low,String week,String sunrise,String sunset,LongWritable aqi,String fx,String fl,String type,String notice){
        this.high = high;
        this.low = low;
        this.week = week;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.aqi = aqi;
        this.fl = fl;
        this.fx = fx;
        this.type = type;
        this.notice = notice;

    }
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(high);
        dataOutput.writeUTF(low);
        dataOutput.writeUTF(week);
        dataOutput.writeUTF(sunrise);
        dataOutput.writeUTF(sunset);
        dataOutput.writeLong(aqi.get());
        dataOutput.writeUTF(fl);
        dataOutput.writeUTF(fx);
        dataOutput.writeUTF(type);
        dataOutput.writeUTF(notice);
    }
    public void readFields(DataInput dataInput) throws IOException {
        high = dataInput.readUTF();
        low = dataInput.readUTF();
        week = dataInput.readUTF();
        sunrise = dataInput.readUTF();
        sunset = dataInput.readUTF();
        this.aqi = new LongWritable();
        this.aqi.readFields(dataInput);
        fl = dataInput.readUTF();
        fx = dataInput.readUTF();
        type = dataInput.readUTF();
        notice = dataInput.readUTF();
    }
    public String toString() {
        return high+ "\t" +low+"\t" +week+ "\t" +sunrise+ "\t" + sunset+ "\t" +aqi+ "\t" +fx+ "\t" +fl+ "\t" +type+ "\t" +notice;
    }

```
* 以下是处理好的数据，这里便截图演示
  ![image](https://github.com/414818141418181/Bigdata_Weather/assets/128785226/f7c8664e-0e0c-4348-aef0-2679a8182c29)
* 接下来我们在hive里创建数据库与表
```
create database Test;

use Test;

create  external table beij(
    ymd string,
    high string,
    low string,
    week string,
    sunrise string,
    sunset string,
    aqi int,
    fx string,
    fl string,
    type string,
    notice string)
row format delimited fields terminated by '\t'
collection items terminated by ' '
stored as textfile;

show tables ;
```
* 这里推荐使用DG，原生hive不太好用，可以看见表已经被创建出来
* 这里我们创建外部表，实际开发中都使用外部表，防止误操作
![image](https://github.com/414818141418181/Bigdata_Weather/assets/128785226/aebe1a83-b309-4235-a471-4fe38d5326ec)

