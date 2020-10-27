package software.amazon.smithy.eventstreamrpc.java;

import com.squareup.javapoet.ClassName;
import software.amazon.smithy.model.shapes.Shape;

import java.util.Optional;

/**
 * Represents a shape that exists in the service that may be real or implicit.
 *
 * A real shape is explicitly defined in the Smithy service model, and an implicit
 * one is automatically generated for an input or output for an operation that does
 * not have an explicitly defined shaped.
 */
public class DataModelObject {
    private final ClassName className;
    private final Optional<Shape> dataShape;
    private final String applicationModelType;

    public DataModelObject(ClassName className, Optional<Shape> dataShape, String applicationModelType) {
        this.className = className;
        this.dataShape = dataShape;
        this.applicationModelType = applicationModelType;
    }

    public ClassName getClassName() {
        return className;
    }

    public Optional<Shape> getDataShape() {
        return dataShape;
    }

    public String getApplicationModelType() {
        return applicationModelType;
    }
}
