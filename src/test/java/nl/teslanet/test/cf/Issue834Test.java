package nl.teslanet.test.cf;

import static org.junit.Assert.assertEquals;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class Issue834Test
{
	private static final int SMALL_CONTENT_SIZE = 10;
	private static final int LARGE_CONTENT_SIZE = 8192;
	private static CoapServer server= null;
	private CoapClient client= null;
	
	/**
	 * Start server
	 */
	@BeforeClass
	public static void setupServer()
	{
		server= new CoapServer();
		server.add(new PayloadLengthResource("return_payload_length"));
		server.start();
	}
	
	/**
	 * Stop server
	 */
	@AfterClass
	public static void tearDownServer()
	{
		if ( server != null )
		{
			server.stop();
			server.destroy();
			server= null;
		}
	}
	
	/**
	 * create client
	 */
	@Before
	public void setupClient()
	{
		client= new CoapClient( "coap://127.0.0.1/return_payload_length");
	}
	
	/**
	 * destroy client
	 */
	@After
	public void tearDownClient()
	{
		if ( client != null )
		{
			client.shutdown();
			client= null;
		}
	}
	
	/**
	 * test service using given payload and assert returned responsecode and payload length
	 * @param code is the request code to use
	 * @param payload  to use in the request
	 * @param expect is the expected response code
	 */
	public void runTestCase( Code code, byte[] payload, ResponseCode expect )
	{
		Request request= new Request( code );
		request.setPayload( payload );
		
		CoapResponse response = client.advanced(request);
		
		assertEquals( "wrong responsecode: ", expect, response.getCode());
		assertEquals( "wrong content length returned: ", payload.length, Integer.parseInt( response.getResponseText()));
	}
	
	/**
	 * test get with small payload
	 */
	@Test
	public void testGetWithSmallPayload()
	{
		runTestCase( Code.GET, getSmallContent(), ResponseCode.CONTENT);
	}

	/**
	 * test post with small payload
	 */
	@Test
	public void testPostWithSmallPayload()
	{
		runTestCase( Code.POST, getSmallContent(), ResponseCode.CREATED );
	}

	/**
	 * test put with small payload
	 */
	@Test
	public void testPutWithSmallPayload()
	{
		runTestCase( Code.PUT, getSmallContent(), ResponseCode.CHANGED );
	}

	/**
	 * test delete with small payload
	 */
	@Test
	public void testDeleteWithSmallPayload()
	{
		runTestCase( Code.DELETE, getSmallContent(), ResponseCode.DELETED );
	}

	/**
	 * test fetch with small payload
	 */
	@Test
	public void testFetchWithSmallPayload()
	{
		runTestCase( Code.FETCH, getSmallContent(), ResponseCode.CONTENT );
	}

	/**
	 * test patch with small payload
	 */
	@Test
	public void testPatchWithSmallPayload()
	{
		runTestCase( Code.PATCH, getSmallContent(), ResponseCode.CHANGED );
	}

	/**
	 * test get with large payload
	 */
	@Test
	public void testGetWithLargePayload()
	{
		runTestCase( Code.GET, getLargeContent(), ResponseCode.CONTENT);
	}

	/**
	 * test post with large payload
	 */
	@Test
	public void testPostWithLargePayload()
	{
		runTestCase( Code.POST, getLargeContent(), ResponseCode.CREATED );
	}

	/**
	 * test put with large payload
	 */
	@Test
	public void testPutWithLargePayload()
	{
		runTestCase( Code.PUT, getLargeContent(), ResponseCode.CHANGED );
	}
	
	/**
	 * test delete with large payload
	 */
	@Test
	public void testDeleteWithLargePayload()
	{
		runTestCase( Code.DELETE, getLargeContent(), ResponseCode.DELETED );
	}


	/**
	 * test fetch with larege payload
	 */
	@Test
	public void testFetchWithLargePayload()
	{
		runTestCase( Code.FETCH, getLargeContent(), ResponseCode.CONTENT );
	}
	

	/**
	 * test patch with large payload
	 */
	@Test
	public void testPatchWithLargePayload()
	{
		runTestCase( Code.PATCH, getLargeContent(), ResponseCode.CHANGED );
	}
	
	/**
     * Create small test content 
     * @return the test content
     */
    public static byte[] getSmallContent()
    {
        byte[] content= new byte [SMALL_CONTENT_SIZE];
        for ( int i= 0; i < SMALL_CONTENT_SIZE; i++ )
        {
            content[i]= (byte) ( i % ( Byte.MAX_VALUE + 1 ) );
        }
        return content;
    }

    /**
     * Create large test content 
     * @return the test content
     */
    public static byte[] getLargeContent()
    {
        byte[] content= new byte [LARGE_CONTENT_SIZE];
        for ( int i= 0; i < LARGE_CONTENT_SIZE; i++ )
        {
            content[i]= (byte) ( i % ( Byte.MAX_VALUE + 1 ) );
        }
        return content;
    }

	/**
	 * Service resource
	 *
	 */
	public static class PayloadLengthResource extends CoapResource
	{
		public PayloadLengthResource(String name) 
		{
			super(name);
		}

		@Override
		public void handleGET( CoapExchange exchange )
		{
			byte[] requestPayload = exchange.getRequestPayload();	
			exchange.respond( ResponseCode.CONTENT, Integer.toString( requestPayload.length ));
		}

		@Override
		public void handlePOST( CoapExchange exchange )
		{
			byte[] requestPayload = exchange.getRequestPayload();
			exchange.respond( ResponseCode.CREATED, Integer.toString( requestPayload.length ));
		}

		@Override
		public void handlePUT( CoapExchange exchange )
		{
			byte[] requestPayload = exchange.getRequestPayload();
			exchange.respond( ResponseCode.CHANGED, Integer.toString( requestPayload.length ));
		}

		@Override
		public void handleDELETE( CoapExchange exchange )
		{
			byte[] requestPayload = exchange.getRequestPayload();
			exchange.respond( ResponseCode.DELETED, Integer.toString( requestPayload.length ));
		}
		
		@Override
		public void handleFETCH( CoapExchange exchange )
		{
			byte[] requestPayload = exchange.getRequestPayload();	
			exchange.respond( ResponseCode.CONTENT, Integer.toString( requestPayload.length ));
		}
		
		@Override
		public void handlePATCH( CoapExchange exchange )
		{
			byte[] requestPayload = exchange.getRequestPayload();	
			exchange.respond( ResponseCode.CHANGED, Integer.toString( requestPayload.length ));
		}
	}
}
