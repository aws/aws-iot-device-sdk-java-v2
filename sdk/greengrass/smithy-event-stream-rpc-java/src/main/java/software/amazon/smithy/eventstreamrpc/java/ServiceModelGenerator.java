package software.amazon.smithy.eventstreamrpc.java;

import com.squareup.javapoet.JavaFile;
import software.amazon.smithy.eventstreamrpc.java.server.OperationContinuationHandlerFactoryBuilder;
import software.amazon.smithy.eventstreamrpc.java.server.AbstractOperationHandlerBuilder;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.OperationShape;

import java.util.function.Consumer;
import java.util.logging.Logger;

public class ServiceModelGenerator implements JavaFileGenerator {
    private static final Logger LOGGER = Logger.getLogger(ServiceModelGenerator.class.getName());

    private final ServiceCodegenContext context;
    private final Model model;

    public ServiceModelGenerator(final ServiceCodegenContext context, final Model model) {
        this.context = context;
        this.model = model;
    }

    /**
     * Kind of a dumb way of saying "produce Java files here for this service, and don't worry about it"
     * @param javaFileConsumer
     */
    @Override
    public void accept(final Consumer<JavaFile> javaFileConsumer) {
        final OperationContinuationHandlerFactoryBuilder operationContinuationHandlerFactoryBuilder = new OperationContinuationHandlerFactoryBuilder(context);
        //generate the OperationContinuationHandlerFactory
        javaFileConsumer.accept(operationContinuationHandlerFactoryBuilder.get());

        //per operation generate abstract operation continuation handlers
        final AbstractOperationHandlerBuilder operationHandlerBuilder = new AbstractOperationHandlerBuilder(context);
        context.getServiceShape().getAllOperations().stream()
                .map(opShapeId -> (OperationShape)context.getModel().getShape(opShapeId).get())
                .forEach(operationShape -> {
                    javaFileConsumer.accept(operationHandlerBuilder.apply(operationShape));
                });
    }

    /**
     * This is not the logical Java package/subdirectory to generate models in, but rather the subdirectory
     * used in the output directory to separate other output using the same plugin invocation
     *
     * @return
     */
    @Override
    public String getOutputSubdirectory() {
        return "server";
    }
}
