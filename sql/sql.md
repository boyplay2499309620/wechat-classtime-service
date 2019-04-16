### 2019.4.415

#### 1、添加点赞数

alter table ranking add column love int(10) default 0 comment'点赞数';

#### 2、删除time表的关于房间号的外键

alter table time drop foreign key time_ibfk_2;