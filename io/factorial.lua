-- defines [a] `factorial` 'function'
    function fact (n)
      if n == 0 then
        return 1
      else
        return n * fact(n-1)
      end
    end

    b = function(t)
      print(t)
      return
    end
    
    b(fact(5))
    -- print(fact(a)) --comment then EOF
