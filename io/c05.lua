a = 1
if a == 0 then
    print("hello")
elseif a == 1 then
    print('elif1')
elseif a > 2 then
    if a == 3 then
        print('nested')
    else
        print('elif2')
    end
else
    print('else stuff')
end