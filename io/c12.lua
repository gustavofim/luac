local a = 10
a = 20

fun = function(a)
    print(a)
    if a then return end
    print('pops')
end

fun(100)
print('----------')
fun()