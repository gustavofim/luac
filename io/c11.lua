a = {}

a.b = {}

a.b.c = {}

a.fun = function()
    print(0)
end

a.b.fun = function()
    return 10
end

a.b.c.fun = function(a)
    return a
end

a.fun()
print(a.b.fun())
print(a.b.c.fun(20))