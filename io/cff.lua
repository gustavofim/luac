a = {}

a.b = function()
    return {66, 77}
end

b = a.b()[1]

print(b)