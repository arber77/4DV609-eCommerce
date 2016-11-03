package org.ecommerce.message.api;

import java.util.UUID;

import org.pcollections.PSequence;
import static com.lightbend.lagom.javadsl.api.Service.named;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import akka.NotUsed;
import akka.stream.javadsl.Source;

public interface MessageService extends Service {

	
	ServiceCall<NotUsed, Message> getMessage(String messageId);


	ServiceCall<CreateMessageRequest, CreateMessageResponse> sendMessage();
	
	
	ServiceCall<NotUsed, PSequence<Message>> getAllMessages();
	
	
	ServiceCall<NotUsed, PSequence<Message>> getAllMessagesbyItemId(String itemId);
	
	

	@Override
	default Descriptor descriptor() {
		return Service.named("MESSAGE-SERVICE").withCalls(
				Service.restCall(Method.POST, "/api/message/send", this::sendMessage),
				Service.restCall(Method.GET, "/api/message/:messageId", this::getMessage),
				Service.restCall(Method.GET,"/api/message/all/list", this::getAllMessages),
				Service.restCall(Method.GET,"/api/message/all/:itemId", this::getAllMessagesbyItemId))
				.withAutoAcl(true);
	}
}
