#!
exec scriptly lua "$0" "$@"
!#

--[[

org.luaj:luaj-jse:3.0.1

]]

-- I don't know if there is an easier way to do this in Luaj
function toTable(list)
  local temp = {}

  for i = 1, #list do
    temp[i] = list[i]
  end

  return temp
end


print(table.concat(toTable(args), ', ') .. " from Lua!")


