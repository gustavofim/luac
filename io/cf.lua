local object = {
    a = function(self)
        print("method1 called with ")
        return self
    end,
    b = function(self)
        print("method2 called with ")
        return self
    end
}

-- Chained method calls using multiple nameAndArgs
object.a(object).b(object)