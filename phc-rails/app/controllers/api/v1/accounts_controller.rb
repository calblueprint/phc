class Api::V1::AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token
  before_action :verify_security_token

  def show
    sf_id = params[:sf_id]
    respond_with Account.find_by sf_id: sf_id
  end

  def search
    first_name = request.params[:FirstName]
    last_name = request.params[:LastName]
    cursor = request.params[:cursor]
    result = Account.fuzzy_search({ FirstName: first_name, LastName: last_name }, false)
    result.each do |account|
      if account.sf_id.nil?
        account.sf_id = "None"
      end
    end
    unless cursor.nil?
      result = result[cursor.to_i, 20] # Hardcoded 20 pagination size
    end
    if result.nil?
      respond_with [].to_json
    else
      respond_with result.to_json(only: [:FirstName, :LastName, :Birthdate__c, :sf_id])
    end
  end

  def create
    if not Account.spawn(params).nil?
      respond_with "Successfully saved account!", status: 200, location: root_url
    else
      respond_with "Error saving account!", status: 200, location: root_url
    end
  end

  def duplicates
    attributes = ActiveSupport::JSON.decode(request.params[:attributes])
    if attributes.nil?
      # Default is to match on SSN
      result = Account.select('"SS_Num__c"').group('"SS_Num__c"').having("count(*)>1").count
    else
      # Escape each attribute with quotes to prevent lowercasing by Rails
      # Count does not work if we have multiple fields! poop
      attributes.map! { |x| "\"#{x}\"" }
      result = Account.select(attributes).group(attributes).having("count(*)>1")
    end
    respond_with result.to_json
  end

end
