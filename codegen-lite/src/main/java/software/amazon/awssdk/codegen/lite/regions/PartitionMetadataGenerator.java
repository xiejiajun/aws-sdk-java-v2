/*
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.codegen.lite.regions;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import software.amazon.awssdk.annotations.Generated;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.codegen.lite.PoetClass;
import software.amazon.awssdk.codegen.lite.Utils;
import software.amazon.awssdk.codegen.lite.regions.model.Partition;
import software.amazon.awssdk.codegen.lite.regions.model.Service;

public class PartitionMetadataGenerator implements PoetClass {

    private final Partition partition;
    private final String basePackage;
    private final String regionBasePackage;

    public PartitionMetadataGenerator(Partition partition,
                                      String basePackage,
                                      String regionBasePackage) {
        this.partition = partition;
        this.basePackage = basePackage;
        this.regionBasePackage = regionBasePackage;
    }

    @Override
    public TypeSpec poetClass() {
        TypeName string = ClassName.get(String.class);
        TypeName listOfServicePartitionMetadata =
            ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(regionBasePackage, "ServicePartitionMetadata"));

        return TypeSpec.classBuilder(className())
                       .addModifiers(FINAL, PUBLIC)
                       .addSuperinterface(ClassName.get(regionBasePackage, "PartitionMetadata"))
                       .addAnnotation(SdkPublicApi.class)
                       .addAnnotation(AnnotationSpec.builder(Generated.class)
                                                    .addMember("value",
                                                               "$S",
                                                               "software.amazon.awssdk:codegen")
                                                    .build())
                       .addField(FieldSpec.builder(className(), "INSTANCE")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer("new $T()", className())
                                          .build())
                       .addField(FieldSpec.builder(String.class, "DNS_SUFFIX")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer("$S", partition.getDnsSuffix())
                                          .build())
                       .addField(FieldSpec.builder(String.class, "HOSTNAME")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer("$S", partition.getDefaults().getHostname())
                                          .build())
                       .addField(FieldSpec.builder(String.class, "ID")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer("$S", partition.getPartition())
                                          .build())
                       .addField(FieldSpec.builder(String.class, "NAME")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer("$S", partition.getPartitionName())
                                          .build())
                       .addField(FieldSpec.builder(String.class, "REGION_REGEX")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer("$S", partition.getRegionRegex())
                                          .build())
                       .addField(FieldSpec.builder(listOfServicePartitionMetadata, "SERVICES")
                                          .addModifiers(PRIVATE, FINAL, STATIC)
                                          .initializer(servicesInitializer())
                                          .build())
                       .addMethod(getter(string, "dnsSuffix", "DNS_SUFFIX"))
                       .addMethod(getter(string, "hostname", "HOSTNAME"))
                       .addMethod(getter(string, "id", "ID"))
                       .addMethod(getter(string, "name", "NAME"))
                       .addMethod(getter(string, "regionRegex", "REGION_REGEX"))
                       .addMethod(getter(listOfServicePartitionMetadata, "services", "SERVICES"))
                       .build();
    }

    @Override
    public ClassName className() {
        return ClassName.get(basePackage, Stream.of(partition.getPartition().split("-"))
                                                .map(Utils::capitalize)
                                                .collect(Collectors.joining()) + "PartitionMetadata");
    }

    private MethodSpec getter(TypeName type, String methodName, String field) {
        return MethodSpec.methodBuilder(methodName)
                         .addAnnotation(Override.class)
                         .addModifiers(Modifier.PUBLIC)
                         .returns(type)
                         .addStatement("return $L", field)
                         .build();
    }

    private CodeBlock servicesInitializer() {
        return CodeBlock.builder()
                        .add("$T.unmodifiableList($T.asList(", Collections.class, Arrays.class)
                        .add(commaSeparatedServices())
                        .add("))")
                        .build();
    }

    private CodeBlock commaSeparatedServices() {
        ClassName serviceMetadata = ClassName.get(regionBasePackage, "ServiceMetadata");
        ClassName defaultServicePartitionMetadata = ClassName.get(regionBasePackage + ".internal",
                                                                  "DefaultServicePartitionMetadata");
        return partition.getServices()
                        .entrySet()
                        .stream()
                        .map(p -> CodeBlock.of("new $T($T.of($S), INSTANCE, $L)",
                                               defaultServicePartitionMetadata,
                                               serviceMetadata,
                                               p.getKey(),
                                               regionCodeBlock(p.getValue())))
                        .collect(CodeBlock.joining(","));
    }

    private CodeBlock regionCodeBlock(Service service) {
        ClassName regionClassName = ClassName.get(regionBasePackage, "Region");
        String globalRegionForPartition = service.isRegionalized() && service.isPartitionWideEndpointAvailable()
                                          ? service.getPartitionEndpoint()
                                          : null;
        return globalRegionForPartition == null ? CodeBlock.of("null")
                                                : CodeBlock.of("$T.of($S)", regionClassName, globalRegionForPartition);
    }
}
