module AccountHelper
  def format_hash hash
    result = []
    hash.each do |key, value|
      if value.nil? or value.empty?
        value = "None"
      end
      result.append("#{key}: #{value}")
    end
    return result.join(", ")
  end
end
