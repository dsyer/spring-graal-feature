package com.example.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.PatternValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication(proxyBeanMethods = false)
@EnableConfigurationProperties(Foo.class)
public class ValidatorApplication {

	public ValidatorApplication(Validator validator, Foo foo) {
		System.err.println("Valid: " + validator.validate(foo));
	}

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(ValidatorApplication.class, args);
		Thread.currentThread().join(); // To be able to measure memory consumption
	}

	@Bean
	@ConditionalOnProperty(prefix = "org.graalvm.nativeimage", name = "imagecode")
	public static LocalValidatorFactoryBean configurationPropertiesValidator() {
		LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
		factoryBean.setConstraintValidatorFactory(new CustomValidatorFactory());
		MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory();
		factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
		return factoryBean;
	}

	@Bean
	public static BeanDefinitionRegistryPostProcessor removeMethodValidationPostProcessor() {
		return new BeanDefinitionRegistryPostProcessor() {

			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			}

			@Override
			public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
				if (registry.containsBeanDefinition("methodValidationPostProcessor")) {
					registry.removeBeanDefinition("methodValidationPostProcessor");
				}
			}

		};
	}

}

class CustomValidatorFactory implements ConstraintValidatorFactory {

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes"})
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {

		if (key == PatternValidator.class) {
			return (T) new PatternValidator();
		} else if (key == NotBlankValidator.class) {
			return (T) new NotBlankValidator();
		}

		return (T) new ConstraintValidator() {

			@Override
			public boolean isValid(Object value, ConstraintValidatorContext context) {
				return true;
			}

		};
	}

	@Override
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
	}
	
}

@ConfigurationProperties("app")
@Validated
class Foo {
	@Pattern(regexp = "[A-Z][a-z]+", message = "Invalid lastname")
	@NotBlank
	private String value;

	public Foo() {
	}

	public Foo(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Foo [value=" + this.value + "]";
	}
}