package com.daliu.classtime.config;

import java.util.concurrent.CountDownLatch;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
 
 
/**
 * redis配置
 * @author pangjianhui 
 *
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {
	
	 /**
     * stringRedisTemplate不支持泛型，它的类型是<String,String>
     * 
     */
	
	
	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
 
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		//添加监听渠道,注释掉代表监听所有消息
		//container.addMessageListener(new RedisExpiredListenerAdapter(), new PatternTopic("__keyevent@0__:expired"));
 
		return container;
	}
 
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
 
	@Bean
	Receiver receiver(CountDownLatch latch) {
		return new Receiver(latch);
	}
 
	@Bean
	CountDownLatch latch() {
		return new CountDownLatch(1);
	}
 
	@Bean
	StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
		//return new StringRedisTemplate(connectionFactory);
		/**
	     * 使用Jackson2JsonRedisSerialize 替换默认序列化
	     */
		return new StringRedisTemplate(connectionFactory);
	}
	
	/**
	 * 这个RedisTemplate已经配置好了序列化操作
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
        Jackson2JsonRedisSerializer<Object> jacksonSeial = new Jackson2JsonRedisSerializer<Object>(Object.class);

        ObjectMapper om = new ObjectMapper();
        // 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会跑出异常
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jacksonSeial.setObjectMapper(om);

        // 值采用json序列化
        template.setValueSerializer(jacksonSeial);
        //使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jacksonSeial);
        template.afterPropertiesSet();

        return template;
	}
	
	
	public class Receiver { 
		
 
		private CountDownLatch latch;
		
		@Autowired
		public Receiver(CountDownLatch latch) {
		    this.latch = latch;
		}
		
		public void receiveMessage(String message) {
		    latch.countDown();
		}
	}
	
	
}
