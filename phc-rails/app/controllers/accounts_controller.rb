class AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token
#  before_action :verify_security_token

  def duplicates
    attrs = request.params[:attributes]
    attrs = attrs.nil? ? [] : ActiveSupport::JSON.decode(attrs)

    # Account.all(:conditions => { :created_at => (Time.now.midnight - 1.day)..Time.now.midnight})
    @accounts = []
    if attrs == []
      # Default is to match on SSN
      result = Account.select('"SS_Num__c"').group('"SS_Num__c"').having("count(*)>1").count
      result.first(100).each do |ssn, count|
        if ssn != ""
          @accounts.append(Account.where(SS_Num__c: ssn).to_a)
        end
      end
    else
      # Escape each attribute with quotes to prevent lowercasing by Rails
      attrs.map! { |x| "\"#{x}\"" }
      result = Account.select(attrs).group(attrs).having("count(*)>1")
      result.each do |ssn|
        @accounts.append(Account.where(SS_Num__c: ssn).to_a)
      end
    end
    # render json: @accounts.as_json
    # respond_with @accounts.to_json
  end
end
