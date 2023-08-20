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
## 数据预处理:
* 使用mapreduce来对数据做一个初级的处理，以日期为key值，为后期数据可视化做准备
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
  --username root \
  --password 123123 \
  --table beij  \
  --export-dir /weather/beij/ \
  --input-fields-terminated-by "\t"
```
