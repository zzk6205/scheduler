package net.web.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component("helloWorldQuartzJob")
public class HelloWorldQuartzJob {

	public void execute(String[] args) throws Exception {
		for (String arg : args) {
			System.out.print(arg + " ");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()) + " Hello World!");
	}

}
