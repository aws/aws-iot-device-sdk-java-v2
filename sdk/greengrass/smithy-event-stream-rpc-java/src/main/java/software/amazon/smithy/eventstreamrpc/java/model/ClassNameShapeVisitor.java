package software.amazon.smithy.eventstreamrpc.java.model;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import software.amazon.smithy.model.shapes.*;

public class ClassNameShapeVisitor implements ShapeVisitor<ClassName> {
    private final TypeNameShapeVisitor typeNameShapeVisitor;

    public ClassNameShapeVisitor(TypeNameShapeVisitor typeNameShapeVisitor) {
        this.typeNameShapeVisitor = typeNameShapeVisitor;
    }

    protected ClassName convertFromTypeName(Shape shape) {
        TypeName typeName = shape.accept(typeNameShapeVisitor);
        if (typeName instanceof ClassName) {
            return (ClassName)typeName;
        }
        throw new UnsupportedOperationException("No classname exists for " + shape.getType().name());
    }

    @Override
    public ClassName blobShape(BlobShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName booleanShape(BooleanShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName listShape(ListShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName setShape(SetShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName mapShape(MapShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName byteShape(ByteShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName shortShape(ShortShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName integerShape(IntegerShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName longShape(LongShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName floatShape(FloatShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName documentShape(DocumentShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName doubleShape(DoubleShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName bigIntegerShape(BigIntegerShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName bigDecimalShape(BigDecimalShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName operationShape(OperationShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName resourceShape(ResourceShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName serviceShape(ServiceShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName stringShape(StringShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName structureShape(StructureShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName unionShape(UnionShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName memberShape(MemberShape shape) {
        return convertFromTypeName(shape);
    }

    @Override
    public ClassName timestampShape(TimestampShape shape) {
        return convertFromTypeName(shape);
    }
}
