module AccountHelper
  def format_hash(hash)
    result = []
    hash.each do |key, value|
      if value.nil? || value.empty?
        value = "None"
      end
      result.append("#{key}: #{value}")
    end
    result.join(", ")
  end
end
