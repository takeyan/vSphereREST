package xyz.takeyan.vmware;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MultivaluedHashMap;
import org.json.*;




public class App {

    public static void main(String[] args) {

        String user="Administrator@takeyan.xyz";
        String passwd="bot06@JUN";
        App app = new App();
        String token = app.getToken(user,passwd);

        System.out.println("### token = " + token);

        String rc = app.deployVM(token);
        System.out.println("@@@ " + rc );

    }


public String deployVM(String token) {

        String vmSpec = "{ target : { resource_pool_id: resgroup-8  },  deployment_spec: {  default_datastore_id: datastore-10,  name: ubuntu1804_03, storage_provisioning: thin, accept_all_EULA: true  } }";

        JSONObject jso = new JSONObject(vmSpec);
	Client client = ClientBuilder.newClient();

//        WebTarget target = client.target("https://vcsa.takeyan.xyz/rest/com/vmware/vcenter/ovf/library-item/id:4116484d-6306-4e2c-9eb7-da3e56b3629a?~action=deploy");
        WebTarget target = client.target("https://vcsa.takeyan.xyz/").path("rest/com/vmware/vcenter/ovf/library-item/id:{id}").queryParam("~action","deploy");
	target = target.resolveTemplate("id","4116484d-6306-4e2c-9eb7-da3e56b3629a");

//        System.out.println("+++ " + target.toString());

        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        builder.header("vmware-api-session-id", token);
        builder.header("Content-Type", "application/json");
        String response = builder.accept(MediaType.APPLICATION_JSON).post(Entity.entity(jso.toString(),"application/json"), String.class);

       return response;
    }



public String getToken(String user, String passwd) {

        String user_api = user + ":" + passwd;
        String auth_header = "Basic " + java.util.Base64.getEncoder().encodeToString( user_api.getBytes() );

        Client client = ClientBuilder.newClient();

        WebTarget target = client.target("https://vcsa.takeyan.xyz/").path("rest/com/vmware/cis/session").queryParam("~method","post");
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
        builder.header("Authorization", auth_header);
        builder.header("vmware-api-session-id", "null");
        builder.header("vmware-use-header-authn", "test");
        builder.header("Content-Type", "application/json");
        Response response = builder.get();

        String st = response.readEntity(String.class);
                JSONObject jo = new JSONObject(st);
                String token = jo.getString("value");
                response.close();

    return token;
    }
}


