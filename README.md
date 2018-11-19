##### 注意

```

1.项目中@Aspect/@Transactional采用spring AOP动态代理模式。@Aspect默认jdk代理，@Transactional默认cglib代理。
2.TransactionManager, TransactionInterceptor使用spring auto-configuration自动生成
3.RedisTemplate，StringRedisTemplate使用spring auto-configuration自动生成

```
