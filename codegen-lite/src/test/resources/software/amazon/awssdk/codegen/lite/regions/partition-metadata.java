package software.amazon.awssdk.regions.partitionmetadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.Generated;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServicePartitionMetadata;
import software.amazon.awssdk.regions.internal.DefaultServicePartitionMetadata;

@SdkPublicApi
@Generated("software.amazon.awssdk:codegen")
public final class AwsPartitionMetadata implements PartitionMetadata {
    private static final AwsPartitionMetadata INSTANCE = new AwsPartitionMetadata();

    private static final String DNS_SUFFIX = "amazonaws.com";

    private static final String HOSTNAME = "{service}.{region}.{dnsSuffix}";

    private static final String ID = "aws";

    private static final String NAME = "AWS Standard";

    private static final String REGION_REGEX = "^(us|eu|ap|sa|ca)\\-\\w+\\-\\d+$";

    private static final List<ServicePartitionMetadata> SERVICES = Collections.unmodifiableList(Arrays.asList(
        new DefaultServicePartitionMetadata(ServiceMetadata.of("a4b"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("acm"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("acm-pca"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("api.mediatailor"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("api.pricing"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("apigateway"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("application-autoscaling"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("appstream2"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("athena"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("autoscaling"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("autoscaling-plans"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("batch"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("budgets"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("ce"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("cloud9"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("clouddirectory"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cloudformation"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cloudfront"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cloudhsm"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cloudhsmv2"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cloudsearch"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cloudtrail"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("codebuild"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("codecommit"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("codedeploy"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("codepipeline"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("codestar"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cognito-identity"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cognito-idp"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cognito-sync"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("comprehend"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("config"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("cur"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("data.iot"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("datapipeline"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("dax"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("devicefarm"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("directconnect"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("discovery"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("dlm"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("dms"), INSTANCE, null), new DefaultServicePartitionMetadata(ServiceMetadata.of("ds"),
                                                                                            INSTANCE, null), new DefaultServicePartitionMetadata(ServiceMetadata.of("dynamodb"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("ec2"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("ecr"), INSTANCE, null), new DefaultServicePartitionMetadata(ServiceMetadata.of("ecs"),
                                                                                            INSTANCE, null), new DefaultServicePartitionMetadata(ServiceMetadata.of("elasticache"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("elasticbeanstalk"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("elasticfilesystem"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("elasticloadbalancing"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("elasticmapreduce"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("elastictranscoder"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("email"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("entitlement.marketplace"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("es"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("events"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("firehose"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("fms"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("gamelift"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("glacier"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("glue"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("greengrass"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("guardduty"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("health"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("iam"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("importexport"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("inspector"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("iot"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("iotanalytics"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("kinesis"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("kinesisanalytics"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("kinesisvideo"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("kms"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("lambda"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("lightsail"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("logs"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("machinelearning"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("macie"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("marketplacecommerceanalytics"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("mediaconvert"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("medialive"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("mediapackage"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("mediastore"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("metering.marketplace"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("mgh"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("mobileanalytics"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("models.lex"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("monitoring"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("mq"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("mturk-requester"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("neptune"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("opsworks"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("opsworks-cm"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("organizations"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("pinpoint"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("polly"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("rds"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("redshift"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("rekognition"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("resource-groups"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("route53"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("route53domains"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("runtime.lex"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("runtime.sagemaker"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("s3"), INSTANCE, Region.of("us-east-1")),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("sagemaker"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("sdb"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("secretsmanager"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("serverlessrepo"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("servicecatalog"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("servicediscovery"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("shield"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("sms"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("snowball"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("sns"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("sqs"), INSTANCE, null), new DefaultServicePartitionMetadata(ServiceMetadata.of("ssm"),
                                                                                            INSTANCE, null), new DefaultServicePartitionMetadata(ServiceMetadata.of("states"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("storagegateway"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("streams.dynamodb"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("sts"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("support"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("swf"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("tagging"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("transcribe"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("translate"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("waf"), INSTANCE, null), new DefaultServicePartitionMetadata(
            ServiceMetadata.of("waf-regional"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("workdocs"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("workmail"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("workspaces"), INSTANCE, null),
        new DefaultServicePartitionMetadata(ServiceMetadata.of("xray"), INSTANCE, null)));

    @Override
    public String dnsSuffix() {
        return DNS_SUFFIX;
    }

    @Override
    public String hostname() {
        return HOSTNAME;
    }

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String regionRegex() {
        return REGION_REGEX;
    }

    @Override
    public List<ServicePartitionMetadata> services() {
        return SERVICES;
    }
}
