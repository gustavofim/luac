a = function(b)
    if b == 0 then
        print("base")
        return
    end
    print(b)
    return a(b-1)
end

print(a(2))
