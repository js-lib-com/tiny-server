package js.tiny.server.bench;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Compare Tiny Server requests processing time with Tomcat and Jetty. Send HTTP requests to a server running three Docker
 * containers - for each tested web server, with the same WAR. Network connection is relatively slow.
 * 
 * To execute benchmark run <code>mvn integration-test</code> from command line. It seems needs <code>mvn clean package</code>
 * before benchmark run.
 * 
 * Results show no difference, maybe because of slow networking. Anyway, we can conclude that all three web servers behave
 * almost the same.
 * 
 * <pre>
 * Benchmark                         Mode  Cnt   Score   Error  Units
 * RequestProcessingTime.jetty       avgt   20  38.491 ▒ 0.057  ms/op
 * RequestProcessingTime.tinyServer  avgt   20  38.940 ▒ 1.140  ms/op
 * RequestProcessingTime.tomcat      avgt   20  38.445 ▒ 0.090  ms/op
 * </pre>
 *
 * Disclaimer: Java Microbenchmark Harness - JMH was used simply because it was handy but for sure HTTP request is not a use
 * case for JHM. Anyway, for macro comparison results are still valid.
 * 
 * @author Iulian Rotaru
 */
@State(Scope.Benchmark)
public class RequestProcessingTime {
	public static void main(String[] args) throws Exception {
		Options options = new OptionsBuilder()//
				.include(RequestProcessingTime.class.getSimpleName())//
				.mode(Mode.AverageTime)//
				.forks(1)//
				.warmupIterations(2)//
				.measurementIterations(20)//
				.timeUnit(TimeUnit.MILLISECONDS)//
				.build();
		new Runner(options).run();
	}

	private final HttpClientBuilder clientBuilder;

	public RequestProcessingTime() {
		clientBuilder = HttpClients.custom();
	}

	@Benchmark
	public String tinyServer() throws IOException {
		return exercise(7073);
	}

	@Benchmark
	public String tomcat() throws IOException {
		return exercise(7074);
	}

	@Benchmark
	public String jetty() throws IOException {
		return exercise(7077);
	}

	private String exercise(int port) throws IOException {
		try (CloseableHttpClient client = clientBuilder.build()) {
			HttpPost httpPost = new HttpPost(URI.create(String.format("http://10.138.44.35:%d/com/jslib/demo/Service/hello.rmi", port)));
			httpPost.setEntity(new StringEntity("[\"Iulian Rotaru\"]", ContentType.APPLICATION_JSON));

			try (CloseableHttpResponse response = client.execute(httpPost)) {
				if (response.getStatusLine().getStatusCode() != 200) {
					throw new IOException("Server error.");
				}
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		}
	}
}
