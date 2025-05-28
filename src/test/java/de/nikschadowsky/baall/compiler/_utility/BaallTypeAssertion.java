package de.nikschadowsky.baall.compiler._utility;

import de.nikschadowsky.baall.compiler.semantic.type.*;

public class BaallTypeAssertion extends BaseAssertion<BaallTypeAssertion, BaallType> {

    protected BaallTypeAssertion(BaallType actual) {
        super(actual, BaallTypeAssertion.class);
    }

    public BaallTypeAssertion isPrimitive() {
        return myself.isInstanceOf(PrimitiveType.class);
    }

    public BaallTypeAssertion isFunction() {
        return myself.isInstanceOf(FunctionType.class);
    }

    public BaallTypeAssertion isList() {
        return myself.isInstanceOf(ListType.class);
    }

    public BaallTypeAssertion isUserType() {
        return myself.isInstanceOf(UserDefinedType.class);
    }


}
