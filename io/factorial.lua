-- defines [a] `factorial` 'function'
    fact = function(n)
      if n == 0 then
        return 1
      else
        return n * fact(n-1)
      end
    end
    print(fact(10))
    -- print(fact(a)) --comment then EOF
