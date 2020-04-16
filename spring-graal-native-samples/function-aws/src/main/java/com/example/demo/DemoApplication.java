package com.example.demo;

import java.util.function.Function;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(proxyBeanMethods = false)
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public Foobar foobar() {
		return new Foobar();
	}

}

class Foo {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Foo(String name) {
		this.name = name;
	}

	Foo() {}
}

class Foobar implements Function<Foo, Foo> {

    @Override
    public Foo apply(Foo input) {
        System.err.println("HI: " + input.getName());
        return new Foo("hi " + input + "!");
    }
}