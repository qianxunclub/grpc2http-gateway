package com.qianxunclub.grpchttpgateway.service;


import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.qianxunclub.grpchttpgateway.configuration.GrpcEndpointProperties;
import com.qianxunclub.grpchttpgateway.configuration.SwaggerProperties;
import com.qianxunclub.grpchttpgateway.grpc.ServiceResolver;
import com.qianxunclub.grpchttpgateway.utils.FieldTypeEnum;
import com.qianxunclub.grpchttpgateway.utils.GrpcServiceUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class OpenApiService {

    private final GrpcEndpointProperties grpcEndpointProperties;
    private final SwaggerProperties swaggerProperties;


    public OpenAPI openApi(String serverName) throws Exception {

        Info info = new Info();
        info.title(serverName);
        info.description(grpcEndpointProperties.getAllEndpoint().get(serverName));

        List<Tag> tags = new ArrayList<>();

        List<Server> servers = new ArrayList<>();
        Server server = new Server();
        server.setUrl(swaggerProperties.getServerUrl() + "/api/" + serverName);
        servers.add(server);

        Paths paths = new Paths();

        List<DescriptorProtos.FileDescriptorSet> fileDescriptorSetList = GrpcServiceUtils
                .getFileDescriptorSetList(grpcEndpointProperties.getEndpoint(serverName));
        Map<String, Schema> schemas = new HashMap<>();
        fileDescriptorSetList.forEach(fileDescriptorSet -> {
            ServiceResolver serviceResolver = ServiceResolver.fromFileDescriptorSet(fileDescriptorSet);

            serviceResolver.listMessageTypes().forEach(descriptor -> {
                schemas.putAll(this.parseFields(descriptor));
            });

            serviceResolver.listServices().forEach(serviceDescriptor -> {

                Tag tag = new Tag();
                tag.setName(serviceDescriptor.getName());
                tag.setDescription(serviceDescriptor.getFullName());
                tags.add(tag);

                serviceDescriptor.getMethods().forEach(methodDescriptor -> {

                    //接口封装
                    PathItem pathItem = new PathItem();
                    Operation operation = new Operation();
                    operation.addTagsItem(serviceDescriptor.getName());
                    operation.setDescription(methodDescriptor.getName());
                    operation.setRequestBody(this.getRequestBody(methodDescriptor.getInputType()));
                    operation.setResponses(this.getApiResponses(methodDescriptor.getInputType()));
                    pathItem.post(operation);
                    paths.addPathItem("/" + methodDescriptor.getFullName(), pathItem);
                });
            });

        });


        OpenAPI oas = new OpenAPI();

        oas.info(info);
        oas.tags(tags);
        oas.servers(servers);
        oas.paths(paths);
        Components components = new Components();
        components.schemas(schemas);
        oas.components(components);
        return oas;
    }


    public RequestBody getRequestBody(Descriptors.Descriptor descriptor) {
        RequestBody requestBody = new RequestBody();
        requestBody.setDescription(descriptor.getName());

        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema schema = new Schema();
        schema.set$ref(this.get$ref(descriptor.getFullName()));
        mediaType.setSchema(schema);

        content.addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, mediaType);
        requestBody.setContent(content);

        return requestBody;
    }

    public ApiResponses getApiResponses(Descriptors.Descriptor descriptor) {
        ApiResponses apiResponses = new ApiResponses();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.set$ref(this.get$ref(descriptor.getFullName()));

        apiResponses.addApiResponse("200", apiResponse);
        return apiResponses;
    }

    public Map<String, Schema> parseFields(Descriptors.Descriptor descriptor) {
        List<Descriptors.FieldDescriptor> fieldDescriptorList = descriptor.getFields();

        Map<String, Schema> schemaMap = new HashMap<>();

        Schema schema = new Schema();
        schema.setTitle(descriptor.getName());
        fieldDescriptorList.forEach(fieldDescriptor -> {
            schema.addProperties(fieldDescriptor.getName(), this.parseField(fieldDescriptor));
        });
        schemaMap.put(descriptor.getFullName(), schema);
        return schemaMap;
    }

    public Schema parseField(Descriptors.FieldDescriptor fieldDescriptor) {
        Schema schema;
        if (fieldDescriptor.isRepeated()) {
            // map
            if (fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && fieldDescriptor.getMessageType().getOptions().getMapEntry()) {
                schema = new MapSchema();
                schema.setAdditionalProperties(this.parseField(fieldDescriptor.getMessageType().getFields().get(1)));
            } else if (fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
                schema = new ArraySchema();
                Schema items = new Schema();
                List<String> enums = new ArrayList<>();
                Descriptors.EnumDescriptor enumDescriptor = fieldDescriptor.getEnumType();
                enumDescriptor.getValues().forEach(enumValueDescriptor -> enums.add(enumValueDescriptor.getName()));
                items.setEnum(enums);
                items.setType(FieldTypeEnum.getJavaSimpleType(fieldDescriptor.getType().name()));
                ((ArraySchema) schema).setItems(items);
            } else { // array
                schema = new ArraySchema();
                Schema items = new Schema();
                items.setFormat(fieldDescriptor.getType().name());
                items.setType(FieldTypeEnum.getJavaSimpleType(fieldDescriptor.getType().name()));
                if (fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
                    items.set$ref(this.get$ref(fieldDescriptor.getMessageType().getFullName()));
                }
                ((ArraySchema) schema).setItems(items);
            }
        } else if (fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.MESSAGE) {
            schema = new ObjectSchema();
            schema.set$ref(this.get$ref(fieldDescriptor.getMessageType().getFullName()));
        } else if (fieldDescriptor.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            schema = new Schema();
            List<String> enums = new ArrayList<>();
            Descriptors.EnumDescriptor enumDescriptor = fieldDescriptor.getEnumType();
            enumDescriptor.getValues().forEach(enumValueDescriptor -> enums.add(enumValueDescriptor.getName()));
            schema.setEnum(enums);
            schema.setType(FieldTypeEnum.getJavaSimpleType(fieldDescriptor.getType().name()));
        } else {
            schema = new Schema();
            schema.setFormat(fieldDescriptor.getType().name());
            schema.setType(FieldTypeEnum.getJavaSimpleType(fieldDescriptor.getType().name()));
        }
        schema.setTitle(fieldDescriptor.getName());
        return schema;
    }


    public String get$ref(String fullName) {
        return "#/components/schemas/" + fullName;
    }

}
